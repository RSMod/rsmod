version = "1.3.0"

plugins {
    `maven-publish`
    signing
    kotlin("jvm")
    id("me.champeau.gradle.jmh") apply true
}

dependencies {
    jmh("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    jmh("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
}

jmh {
    profilers = listOf("stack")
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
        pom {
            packaging = "jar"
            name.set("RS Mod Pathfinder")
            description.set(
                """
                A custom BFS pathfinder implementation to emulate RS.
                """.trimIndent()
            )
        }
        signing {
            useGpgCmd()
            sign(publishing.publications["maven"])
        }
    }
}
