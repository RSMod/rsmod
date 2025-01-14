package org.rsmod.game.entity

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList
import org.rsmod.game.client.Client
import org.rsmod.game.client.NoopClient
import org.rsmod.game.entity.player.PublicMessage
import org.rsmod.game.entity.shared.PathingEntityCommon
import org.rsmod.game.inv.Inventory
import org.rsmod.game.inv.InventoryMap
import org.rsmod.game.queue.PlayerQueueList
import org.rsmod.game.queue.QueueCategory
import org.rsmod.game.seq.EntitySeq
import org.rsmod.game.shop.Shop
import org.rsmod.game.stat.PlayerStatMap
import org.rsmod.game.timer.PlayerTimerMap
import org.rsmod.game.type.droptrig.DropTriggerType
import org.rsmod.game.type.mod.ModGroup
import org.rsmod.game.type.queue.QueueType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.timer.TimerType
import org.rsmod.game.ui.UserInterfaceMap
import org.rsmod.map.CoordGrid
import org.rsmod.routefinder.collision.CollisionStrategy
import org.rsmod.routefinder.flag.CollisionFlag

public class Player(
    public var client: Client<Any, Any> = NoopClient,
    override val avatar: PlayerAvatar = PlayerAvatar(),
) : PathingEntity() {
    init {
        pendingFaceSquare = CoordGrid.ZERO
        pendingSequence = EntitySeq.ZERO
    }

    public override val isBusy: Boolean
        get() = isDelayed || ui.modals.isNotEmpty()

    override val collisionStrategy: CollisionStrategy = CollisionStrategy.Normal

    override val blockWalkCollisionFlag: Int = CollisionFlag.BLOCK_NPCS

    public val ui: UserInterfaceMap = UserInterfaceMap()
    public val invMap: InventoryMap = InventoryMap()
    public val statMap: PlayerStatMap = PlayerStatMap()
    public val timerMap: PlayerTimerMap = PlayerTimerMap()
    public val softTimerMap: PlayerTimerMap = PlayerTimerMap()
    public val queueList: PlayerQueueList = PlayerQueueList()
    public val weakQueueList: PlayerQueueList = PlayerQueueList()

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

    public var actionDelay: Int = -1
    public var skillAnimDelay: Int = -1
    public var skillSoundDelay: Int = -1

    public var lootDropDuration: Int? = null

    /**
     * Drop triggers enable extensibility for inv obj drop prevention.
     *
     * They are best suited for controlled, enclosed environments such as minigames, raids,
     * instances, or other scenarios tied to a specific area. The drop trigger is reset only when
     * the player drops an inventory obj or when explicitly cleared via [clearDropTrigger] or
     * [clearAnyDropTrigger].
     *
     * If there is no clear mechanism to reset the trigger - such as an `exit` function for a
     * minigame - there is no guarantee that the drop trigger will not become "out-of-date" until
     * the player drops an inventory object.
     *
     * _Note: Use the [dropTrigger] function to set this value._
     */
    public var dropTrigger: DropTriggerType? = null
        private set

    public fun timer(timer: TimerType, cycles: Int) {
        timerMap[timer] = currentMapClock + cycles
    }

    public fun softTimer(timer: TimerType, cycles: Int) {
        softTimerMap[timer] = currentMapClock + cycles
    }

    public fun weakQueue(queue: QueueType, cycles: Int, args: Any? = null) {
        weakQueueList.add(queue, QueueCategory.Weak, cycles, args)
    }

    public fun softQueue(queue: QueueType, cycles: Int, args: Any? = null) {
        queueList.add(queue, QueueCategory.Soft, cycles, args)
    }

    public fun queue(queue: QueueType, cycles: Int, args: Any? = null) {
        queueList.add(queue, QueueCategory.Normal, cycles, args)
    }

    public fun strongQueue(queue: QueueType, cycles: Int, args: Any? = null) {
        queueList.add(queue, QueueCategory.Strong, cycles, args)
    }

    public fun longQueueAccelerate(queue: QueueType, cycles: Int, args: Any? = null) {
        queueList.add(queue, QueueCategory.LongAccelerate, cycles, args)
    }

    public fun longQueueDiscard(queue: QueueType, cycles: Int, args: Any? = null) {
        queueList.add(queue, QueueCategory.LongDiscard, cycles, args)
    }

    override fun anim(seq: SeqType, delay: Int, priority: Int) {
        PathingEntityCommon.anim(this, seq, delay, priority)
    }

    public fun facePlayer(target: Player): Unit = PathingEntityCommon.facePlayer(this, target)

    public fun faceNpc(target: Npc): Unit = PathingEntityCommon.faceNpc(this, target)

    public fun resetFaceEntity(): Unit = PathingEntityCommon.resetFaceEntity(this)

    /**
     * @throws [IllegalStateException] if a [dropTrigger] is already set. This ensures that
     *   previously set drop triggers cannot be replaced unexpectedly or removed without explicit
     *   action.
     *
     * Features that set a drop trigger are responsible for clearing it using [clearDropTrigger] or
     * [clearAnyDropTrigger]. If a [dropTrigger] is still set, it may indicate the player exited
     * through unintended means, and an error will be thrown to prevent silent failures.
     */
    public fun dropTrigger(trigger: DropTriggerType) {
        // Ensures the player's drop trigger cannot be replaced unexpectedly or through unintended
        // mechanics.
        check(dropTrigger == null) {
            "Previous `dropTrigger` must be removed before " +
                "setting a new trigger: oldTrigger=$dropTrigger, newTrigger=$trigger"
        }
        dropTrigger = trigger
    }

    /**
     * Clears [dropTrigger] as long as it matches [trigger], otherwise throws
     * [IllegalStateException].
     */
    public fun clearDropTrigger(trigger: DropTriggerType) {
        check(dropTrigger == trigger) {
            "Current `dropTrigger` does not match input: " +
                "currentTrigger=$dropTrigger, clearTrigger=$trigger"
        }
        dropTrigger = null
    }

    public fun clearAnyDropTrigger() {
        dropTrigger = null
    }

    override fun toString(): String =
        "Player(username=$username, displayName=$displayName, coords=$coords)"
}
