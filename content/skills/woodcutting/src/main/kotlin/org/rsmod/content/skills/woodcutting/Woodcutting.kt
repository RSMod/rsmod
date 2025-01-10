package org.rsmod.content.skills.woodcutting

import jakarta.inject.Inject
import org.rsmod.api.config.locParam
import org.rsmod.api.config.locXpParam
import org.rsmod.api.config.objParam
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.righthand
import org.rsmod.api.player.stat.woodcuttingLvl
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.script.onOpLoc3
import org.rsmod.events.UnboundEvent
import org.rsmod.game.entity.Player
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

// TODO:
// - sounds
// - bird nests
// - axe effects/charges
class Woodcutting
@Inject
constructor(private val objTypes: ObjTypeList, private val locRepo: LocRepository) :
    PluginScript() {
    override fun ScriptContext.startUp() {
        onOpLoc1(content.tree) { attempt(it.bound, it.type) }
        onOpLoc3(content.tree) { cut(it.bound, it.type) }
    }

    private fun ProtectedAccess.attempt(tree: BoundLocInfo, type: UnpackedLocType) {
        if (player.woodcuttingLvl < type.treeLevelReq) {
            resetAnim()
            mes("You need a Woodcutting level of ${type.treeLevelReq} to chop down this tree.")
            return
        }

        if (inv.isFull()) {
            resetAnim()
            val product = objTypes[type.treeLogs]
            mes("Your inventory is too full to hold any more ${product.name.lowercase()}.")
            return
        }

        if (actionDelay < mapClock) {
            actionDelay = mapClock + 3
            skillAnimDelay = mapClock + 3
            opLoc1(tree)
        } else {
            val axe = findAxe(player, objTypes)
            if (axe == null) {
                mes("You need an axe to chop down this tree.")
                mes("You do not have an axe which you have the woodcutting level to use.")
                return
            }
            anim(objTypes[axe].axeWoodcuttingAnim)
            spam("You swing your axe at the tree.")
            cut(tree, type)
        }
    }

    private fun ProtectedAccess.cut(tree: BoundLocInfo, type: UnpackedLocType) {
        val axe = findAxe(player, objTypes)
        if (axe == null) {
            resetAnim()
            mes("You need an axe to chop down this tree.")
            mes("You do not have an axe which you have the woodcutting level to use.")
            return
        }

        if (player.woodcuttingLvl < type.treeLevelReq) {
            resetAnim()
            mes("You need a Woodcutting level of ${type.treeLevelReq} to chop down this tree.")
            return
        }

        if (inv.isFull()) {
            resetAnim()
            val product = objTypes[type.treeLogs]
            mes("Your inventory is too full to hold any more ${product.name.lowercase()}.")
            return
        }

        if (skillAnimDelay <= mapClock) {
            skillAnimDelay = mapClock + 4
            anim(objTypes[axe].axeWoodcuttingAnim)
        }

        var cutLogs = false
        var despawn = false

        if (actionDelay < mapClock) {
            actionDelay = mapClock + 3
        } else if (actionDelay == mapClock) {
            cutLogs = true // TODO: Random roll
            despawn = !type.hasDespawnTimer && random.of(1, 255) > type.treeDepleteChance
        }

        if (type.hasDespawnTimer) {
            // TODO: Global/shared despawn timers
            // despawn = ...
        }

        if (cutLogs) {
            val product = objTypes[type.treeLogs]
            spam("You get some ${product.name.lowercase()}.")
            statAdvance(stats.woodcutting, type.treeXp)
            invAdd(inv, product)
            publish(CutLogs(player, tree, product))
        }

        if (despawn) {
            locRepo.change(tree, type.treeStump, type.treeRespawnTime)
            resetAnim()
            return
        }

        opLoc3(tree)
    }

    data class CutLogs(val player: Player, val tree: BoundLocInfo, val product: ObjType) :
        UnboundEvent

    companion object {
        val UnpackedObjType.axeWoodcuttingReq: Int by objParam(params.skill_levelreq)
        val UnpackedObjType.axeWoodcuttingAnim: SeqType by objParam(params.skill_anim)

        val UnpackedLocType.treeLevelReq: Int by locParam(params.skill_levelreq)
        val UnpackedLocType.treeLogs: ObjType by locParam(params.skill_productitem)
        val UnpackedLocType.treeXp: Double by locXpParam(params.skill_xp)
        val UnpackedLocType.treeStump: LocType by locParam(params.next_loc_stage)
        val UnpackedLocType.treeDespawnTime: Int by locParam(params.despawn_time)
        val UnpackedLocType.treeDepleteChance: Int by locParam(params.deplete_chance)
        val UnpackedLocType.treeRespawnTime: Int by locParam(params.respawn_time)

        private val UnpackedLocType.hasDespawnTimer: Boolean
            get() = hasParam(params.despawn_time)

        fun findAxe(player: Player, objTypes: ObjTypeList): InvObj? {
            val worn = player.wornAxe(objTypes)
            val carried = player.carriedAxe(objTypes)
            if (worn != null && carried != null) {
                if (objTypes[worn].axeWoodcuttingReq >= objTypes[carried].axeWoodcuttingReq) {
                    return worn
                }
                return carried
            }
            return worn ?: carried
        }

        private fun Player.wornAxe(objTypes: ObjTypeList): InvObj? {
            return righthand?.let {
                val type = objTypes[it]
                if (type.isUsableAxe(woodcuttingLvl)) {
                    it
                } else {
                    null
                }
            }
        }

        private fun Player.carriedAxe(objTypes: ObjTypeList): InvObj? {
            return inv.filterNotNull { objTypes[it].isUsableAxe(woodcuttingLvl) }
                .maxByOrNull { objTypes[it].axeWoodcuttingReq }
        }

        private fun UnpackedObjType.isUsableAxe(woodcuttingLevel: Int): Boolean =
            isAssociatedWith(content.woodcutting_axe) && woodcuttingLevel >= axeWoodcuttingReq
    }
}
