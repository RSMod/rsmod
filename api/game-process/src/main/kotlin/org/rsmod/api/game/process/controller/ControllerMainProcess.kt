package org.rsmod.api.game.process.controller

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import org.rsmod.api.repo.controller.ControllerRepository
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Controller
import org.rsmod.game.entity.ControllerList

public class ControllerMainProcess
@Inject
constructor(
    private val controllerList: ControllerList,
    private val conRepo: ControllerRepository,
    private val aiTimers: AIConTimerProcessor,
    private val queues: ControllerQueueProcessor,
    private val timers: ControllerTimerProcessor,
    private val mapClock: MapClock,
) {
    private val logger = InlineLogger()

    public fun process() {
        for (controller in controllerList) {
            controller.currentMapClock = mapClock.cycle
            controller.tryOrDelete {
                if (isNotDelayed) {
                    resumePausedProcess()
                    aiTimerProcess()
                    queueProcess()
                    timerProcess()
                }
            }
        }
    }

    private fun Controller.resumePausedProcess() {
        advanceActiveCoroutine()
    }

    private fun Controller.aiTimerProcess() {
        aiTimers.process(this)
    }

    private fun Controller.queueProcess() {
        queues.process(this)
    }

    private fun Controller.timerProcess() {
        timers.process(this)
    }

    private inline fun Controller.tryOrDelete(block: Controller.() -> Unit) =
        try {
            block(this)
        } catch (t: Throwable) {
            conRepo.del(this)
            logger.error(t) { "Error processing main cycle for controller: $this." }
        }
}
