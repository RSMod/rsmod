package org.rsmod.api.registry.player

import jakarta.inject.Inject
import org.rsmod.events.EventBus
import org.rsmod.game.entity.PathingEntity.Companion.INVALID_SLOT
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.entity.player.SessionStateEvent
import org.rsmod.map.zone.ZoneKey
import org.rsmod.routefinder.collision.CollisionFlagMap

public class PlayerRegistry
@Inject
constructor(
    private val playerList: PlayerList,
    private val collision: CollisionFlagMap,
    private val eventBus: EventBus,
) {
    public val zones: ZonePlayerMap = ZonePlayerMap()

    public fun count(): Int = zones.playerCount()

    public fun add(player: Player): PlayerRegistryResult.Add {
        val slot = player.slotId
        if (slot == INVALID_SLOT) {
            return PlayerRegistryResult.AddErrorInvalidSlot
        } else if (playerList[slot] != null) {
            return PlayerRegistryResult.AddErrorSlotInUse(playerList.getValue(slot))
        }
        playerList[slot] = player
        player.slotId = slot
        eventBus.publish(SessionStateEvent.Initialize(player))
        eventBus.publish(SessionStateEvent.LogIn(player))
        return PlayerRegistryResult.AddSuccess
    }

    public fun del(player: Player): PlayerRegistryResult.Delete {
        val slot = player.slotId
        if (slot == INVALID_SLOT) {
            return PlayerRegistryResult.DeleteErrorInvalidSlot
        } else if (playerList[slot] != player) {
            return PlayerRegistryResult.DeleteErrorSlotMismatch(playerList[slot])
        }
        playerList.remove(slot)
        eventBus.publish(SessionStateEvent.LogOut(player))
        player.removeBlockWalkCollision(collision, player.coords)
        zoneDel(player, ZoneKey.from(player.coords))
        player.slotId = INVALID_SLOT
        return PlayerRegistryResult.DeleteSuccess
    }

    public fun change(player: Player, from: ZoneKey, to: ZoneKey) {
        zoneDel(player, from)
        zoneAdd(player, to)
    }

    public fun findAll(zone: ZoneKey): Sequence<Player> {
        val entries = zones[zone] ?: return emptySequence()
        return entries.entries.asSequence()
    }

    private fun zoneDel(player: Player, zone: ZoneKey) {
        if (zone == ZoneKey.NULL) {
            return
        }
        val oldZone = zones[zone]
        oldZone?.remove(player)
    }

    private fun zoneAdd(player: Player, zone: ZoneKey) {
        if (zone == ZoneKey.NULL) {
            return
        }
        val newZone = zones.getOrPut(zone)
        check(player !in newZone) { "Player already registered to zone($zone): $player" }
        newZone.add(player)
    }

    public fun nextFreeSlot(): Int? = playerList.nextFreeSlot()
}