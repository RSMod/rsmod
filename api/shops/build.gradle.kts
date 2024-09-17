plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.bundles.logging)
    implementation(libs.fastutil)
    implementation(libs.guice)
    implementation(projects.api.cache)
    implementation(projects.api.config)
    implementation(projects.api.gameProcess)
    implementation(projects.api.invtx)
    implementation(projects.api.player)
    implementation(projects.api.script)
    implementation(projects.api.type.typeBuilders)
    implementation(projects.api.type.typeReferences)
    implementation(projects.api.type.typeScriptDsl)
    implementation(projects.api.utils.utilsFormat)
    implementation(projects.engine.events)
    implementation(projects.engine.game)
    implementation(projects.engine.objtx)
    implementation(projects.engine.plugin)
    testImplementation(projects.api.testing.testFactory)
    testImplementation(projects.api.testing.testParams)
}
