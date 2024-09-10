package org.rsmod.api.obj.plugin

import jakarta.inject.Inject
import org.rsmod.api.config.Constants
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.synths
import org.rsmod.api.invtx.invAdd
import org.rsmod.api.invtx.invCommit
import org.rsmod.api.player.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.soundSynth
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.advanced.onDefaultOpObj3
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.InvObj
import org.rsmod.game.obj.Obj
import org.rsmod.objtx.TransactionResultList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class ObjTakePlugin @Inject constructor(private val repo: ObjRepository) : PluginScript() {
    override fun ScriptContext.startUp() {
        onDefaultOpObj3 { triggerTake(it.obj) }
    }

    private suspend fun ProtectedAccess.triggerTake(obj: Obj) {
        if (!player.hasInvSpace(obj)) {
            player.mes(Constants.dm_take_invspace)
            return
        }
        player.anim(null)
        if (player.coords != obj.coords) {
            takeFar(obj)
        } else {
            player.soundSynth(synths.pick2)
            player.takeClose(obj)
        }
    }

    private suspend fun ProtectedAccess.takeFar(obj: Obj) {
        delay(1)
        player.anim(seqs.human_pickuptable)
        player.soundSynth(synths.pick2)
        player.takeClose(obj)
    }

    private fun Player.takeClose(obj: Obj) {
        val removed = repo.del(obj)
        if (!removed) {
            mes(Constants.dm_take_taken)
            return
        }
        val take = transaction(obj)
        if (take.failure) {
            mes(Constants.dm_take_invspace)
        } else {
            invCommit(inv, take)
        }
    }

    private fun Player.hasInvSpace(obj: Obj): Boolean = transaction(obj).success

    private fun Player.transaction(obj: Obj): TransactionResultList<InvObj> =
        invAdd(inv, obj.toInvObj(), autoCommit = false)

    @Suppress("DEPRECATION") private fun Obj.toInvObj(): InvObj = InvObj(type, count)
}