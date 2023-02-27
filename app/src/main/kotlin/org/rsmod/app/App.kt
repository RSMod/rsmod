package org.rsmod.app

import com.github.ajalt.clikt.core.CliktCommand
import com.github.michaelbull.logging.InlineLogger
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.util.Modules
import org.rsmod.game.GameBootstrap
import org.rsmod.game.GameModule
import org.rsmod.game.config.GameConfig
import org.rsmod.game.config.GameConfigModule
import org.rsmod.game.model.GameEnv
import org.rsmod.game.scripts.module.KotlinScriptModule
import org.rsmod.game.scripts.module.ModuleBranch
import org.rsmod.game.scripts.module.ModuleScriptLoader
import org.rsmod.game.scripts.plugin.KotlinScriptPlugin
import org.rsmod.game.scripts.plugin.ScriptPluginLoader

private val logger = InlineLogger()

public fun main(args: Array<String>): Unit = AppCommand().main(args)

public class AppCommand : CliktCommand(name = "app") {

    override fun run() {
        val gameConfig = preloadGameConfig()
        val pluginModules = loadPluginModules(gameConfig.env)
        val combined = Modules.combine(GameModule, *pluginModules.toTypedArray())
        val injector = Guice.createInjector(combined)
        loadContentPlugins(injector)
        startUpGame(injector)
    }

    private fun preloadGameConfig(): GameConfig {
        // TODO: find a more ideal way to load game config
        // before the main injector is created.
        val injector = Guice.createInjector(GameConfigModule)
        return injector.getInstance(GameConfig::class.java)
    }

    private fun loadPluginModules(env: GameEnv): List<AbstractModule> {
        val modulePlugins = ModuleScriptLoader.load(KotlinScriptModule::class.java)
        val branchModules = modulePlugins.flatMap { it.branchModules[env.moduleBranch] ?: emptyList() }
        val modules = modulePlugins.flatMap { it.modules } + branchModules
        logger.info {
            "Loaded ${modules.size} module plugin${if (modules.size == 1) "" else "s"}. " +
                "(${branchModules.size} branch-specific)"
        }
        return modules
    }

    private fun loadContentPlugins(injector: Injector) {
        val plugins = ScriptPluginLoader.load(KotlinScriptPlugin::class.java, injector)
        logger.info { "Loaded ${plugins.size} content plugin${if (plugins.size == 1) "" else "s"}." }
    }

    private fun startUpGame(injector: Injector) {
        val bootstrap = injector.getInstance(GameBootstrap::class.java)
        val config = injector.getInstance(GameConfig::class.java)
        logger.info { "Loaded game with config: $config" }
        bootstrap.startUp()
    }

    private val GameEnv.moduleBranch: ModuleBranch
        get() = when (this) {
            GameEnv.Dev -> ModuleBranch.Dev
            GameEnv.Prod -> ModuleBranch.Prod
            GameEnv.Test -> ModuleBranch.Test
        }
}
