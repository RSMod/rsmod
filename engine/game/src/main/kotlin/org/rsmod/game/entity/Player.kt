package org.rsmod.game.entity

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList
import org.rsmod.game.client.Client
import org.rsmod.game.client.NoopClient
import org.rsmod.game.entity.player.PublicMessage
import org.rsmod.game.entity.shared.PathingEntityCommon
import org.rsmod.game.inv.Inventory
import org.rsmod.game.inv.InventoryMap
import org.rsmod.game.shop.Shop
import org.rsmod.game.stat.PlayerStatMap
import org.rsmod.game.type.mod.ModGroup
import org.rsmod.game.ui.UserInterfaceMap
import org.rsmod.map.CoordGrid
import org.rsmod.pathfinder.collision.CollisionStrategy
import org.rsmod.pathfinder.flag.CollisionFlag

public class Player(
    public var client: Client<Any, Any> = NoopClient,
    override val avatar: PlayerAvatar = PlayerAvatar(),
) : PathingEntity() {
    init {
        pendingFaceSquare = CoordGrid.ZERO
    }

    public override val isBusy: Boolean
        get() = isDelayed || ui.modals.isNotEmpty()

    override val collisionStrategy: CollisionStrategy = CollisionStrategy.Normal

    override val blockWalkCollisionFlag: Int = CollisionFlag.BLOCK_NPCS

    public val ui: UserInterfaceMap = UserInterfaceMap()
    public val invMap: InventoryMap = InventoryMap()
    public val statMap: PlayerStatMap = PlayerStatMap()

    /**
     * A unique identifier that should be generated when the player's account is created and then
     * remain persistent forever.
     */
    public var uuid: Long? = null
    /**
     * A unique identifier, typically the same as [uuid], but differs under certain conditions, such
     * as when the player is part of a group. It is specifically used to control visibility for
     * "hidden" entities, like objs that are only visible to certain players.
     */
    public var observerUUID: Long? = null
    public var username: String = ""

    public var buildArea: CoordGrid = CoordGrid.NULL
    public val visibleZoneKeys: IntList = IntArrayList()

    public var modGroup: ModGroup? = null
    public var xpRate: Double = 1.0

    public var publicMessage: PublicMessage? = null

    public val isModalButtonProtected: Boolean
        get() = isDelayed || activeCoroutine?.isSuspended == true

    public var displayName: String
        get() = avatar.name
        set(value) {
            avatar.name = value
        }

    /* Cache for commonly-accessed Invs */
    public lateinit var inv: Inventory
    public lateinit var worn: Inventory

    public var modalInv: Inventory? = null
    public var modalSideInv: Inventory? = null
    public var openedShop: Shop? = null

    public fun facePlayer(target: Player): Unit = PathingEntityCommon.facePlayer(this, target)

    public fun faceNpc(target: Npc): Unit = PathingEntityCommon.faceNpc(this, target)

    public fun resetFaceEntity(): Unit = PathingEntityCommon.resetFaceEntity(this)

    override fun toString(): String =
        "Player(username=$username, displayName=$displayName, coords=$coords)"
}
