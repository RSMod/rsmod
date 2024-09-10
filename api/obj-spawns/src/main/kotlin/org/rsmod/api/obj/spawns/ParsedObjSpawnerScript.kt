package org.rsmod.api.obj.spawns

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.rsmod.api.script.onBootUp
import org.rsmod.plugin.scripts.ScriptContext
import org.rsmod.plugin.scripts.SimplePluginScript
import org.rsmod.scheduler.TaskScheduler

public class ParsedObjSpawnerScript
@Inject
constructor(private val spawner: ParsedObjSpawner, private val scheduler: TaskScheduler) :
    SimplePluginScript() {
    private val logger = InlineLogger()

    private lateinit var staticSpawns: Collection<ParsedObjSpawn>

    override fun ScriptContext.startUp() {
        scheduler.scheduleStaticSpawns()
        onBootUp { spawnStaticSpawns() }
    }

    private fun spawnStaticSpawns() {
        check(::staticSpawns.isInitialized) { "`staticSpawns` must be set." }
        logger.debug { "Spawning static objs..." }
        spawner.spawnAll(staticSpawns)
        logger.info {
            "Spawned ${staticSpawns.size} static obj${if (staticSpawns.size == 1) "" else "s"}."
        }
    }

    private fun TaskScheduler.scheduleStaticSpawns() = scheduleIO { loadStaticSpawns() }

    private fun CoroutineScope.loadStaticSpawns() {
        val spawnListCount = spawner.inputContentsList.size
        val spawns = runBlocking { spawner.loadStaticSpawns() }
        staticSpawns = spawns
        logger.debug {
            "Loaded $spawnListCount spawn list${if (spawnListCount == 1) "" else "s"} " +
                "with a total of ${spawns.size} obj spawn${if (spawns.size == 1) "" else "s"}."
        }
        // No longer need these file content getters, can discard.
        spawner.inputContentsList.clear()
    }
}