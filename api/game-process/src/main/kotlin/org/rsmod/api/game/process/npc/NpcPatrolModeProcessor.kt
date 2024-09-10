package org.rsmod.api.game.process.npc

import jakarta.inject.Inject
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.npc.NpcPatrol
import org.rsmod.pathfinder.collision.CollisionFlagMap

public class NpcPatrolModeProcessor @Inject constructor(private val collision: CollisionFlagMap) {
    public fun process(npc: Npc) {
        val patrol = npc.type.patrol ?: return
        npc.updateIdleTicks()
        npc.patrol(patrol)
    }

    private fun Npc.updateIdleTicks() {
        if (hasMovedPreviousTick) {
            patrolIdleTicks = 0
        } else {
            patrolIdleTicks++
        }
    }

    private fun Npc.patrol(patrol: NpcPatrol) {
        val waypoint = patrol[patrolWaypointIndex % patrol.size]
        if (patrolIdleTicks > IDLE_TELE_DELAY) {
            teleport(collision, waypoint.destination)
        }
        if (coords == waypoint.destination) {
            if (waypoint.pauseDelay > patrolPauseTicks) {
                patrolPauseTicks++
                patrolIdleTicks = 0
            } else {
                patrolPauseTicks = 0
                val nextWaypoint = patrol[++patrolWaypointIndex % patrol.size]
                walk(nextWaypoint.destination)
            }
        } else {
            walk(waypoint.destination)
        }
    }

    public companion object {
        /**
         * The amount of ticks that the patrol npc can wait without moving before it teleports to
         * its next patrol waypoint.
         */
        public const val IDLE_TELE_DELAY: Int = 30
    }
}