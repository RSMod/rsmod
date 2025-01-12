package org.rsmod.api.game.process.npc

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import org.rsmod.api.game.process.npc.mode.NpcModeProcessor
import org.rsmod.api.repo.NpcRevealProcessor
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.NpcList

public class NpcMainProcess
@Inject
constructor(
    private val npcs: NpcList,
    private val reveal: NpcRevealProcessor,
    private val aiTimers: AITimerProcessor,
    private val timers: NpcTimerProcessor,
    private val queues: NpcQueueProcessor,
    private val movement: NpcMovementProcessor,
    private val modes: NpcModeProcessor,
    private val facing: NpcFaceSquareProcessor,
    private val mapClock: MapClock,
) {
    private val logger = InlineLogger()

    public fun process() {
        for (npc in npcs) {
            npc.previousCoords = npc.coords
            npc.currentMapClock = mapClock.cycle
            npc.tryOrDespawn {
                if (isNotDelayed) {
                    resumePausedProcess()
                }
                reveal.process(this)
                if (isNotDelayed) {
                    aiTimerProcess()
                    queueProcess()
                    timerProcess()
                    modeProcess()
                    movementProcess()
                    faceSquareProcess()
                }
            }
        }
    }

    private fun Npc.resumePausedProcess() {
        advanceActiveCoroutine()
    }

    private fun Npc.aiTimerProcess() {
        aiTimers.process(this)
    }

    private fun Npc.queueProcess() {
        queues.process(this)
    }

    private fun Npc.timerProcess() {
        timers.process(this)
    }

    private fun Npc.modeProcess() {
        modes.process(this)
    }

    private fun Npc.movementProcess() {
        movement.process(this)
    }

    private fun Npc.faceSquareProcess() {
        val pending = pendingFaceSquare
        facing.process(this)
        if (faceAngle != -1) {
            infoProtocol.setFaceSquare(pending.x, pending.z, instant = false)
        }
    }

    private inline fun Npc.tryOrDespawn(block: Npc.() -> Unit) =
        try {
            block(this)
        } catch (t: Throwable) {
            forceDespawn()
            logger.error(t) { "Error processing main cycle for npc: $this." }
        }
}
