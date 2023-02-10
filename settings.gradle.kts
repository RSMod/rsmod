import java.nio.file.Files
import java.nio.file.Path

rootProject.name = "rsmod"

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    plugins {
        kotlin("jvm") version "1.7.20"
    }
}

include(
    "buffer",
    "cache",
    "game",
    "game:coroutines",
    "game:events",
    "game:pathfinder",
    "game:plugins",
    "game:protocol",
    "game:types",
    "json",
    "log",
    "plugins",
    "toml",
    "app"
)

includePlugins(project(":plugins"))

fun includePlugins(pluginProject: ProjectDescriptor) {
    val pluginPath = pluginProject.projectDir.toPath()
    Files.walk(pluginPath).forEach {
        if (!Files.isDirectory(it)) {
            return@forEach
        }
        searchPlugin(pluginProject.name, pluginPath, it)
    }
}

fun searchPlugin(parentName: String, pluginRoot: Path, currentPath: Path) {
    val hasBuildFile = Files.exists(currentPath.resolve("build.gradle.kts"))
    if (!hasBuildFile) {
        return
    }
    val relativePath = pluginRoot.relativize(currentPath)
    val pluginName = relativePath.toString().replace(File.separator, ":")
    include("$parentName:$pluginName")
}
