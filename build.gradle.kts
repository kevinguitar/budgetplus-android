@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.toml.checker)
    alias(libs.plugins.toml.updater)
}

tasks.register("clean", Delete::class) { delete(rootProject.buildDir) }

// Collect all the issue for baseline on the project level.
val detektProjectBaseline by tasks.registering(io.gitlab.arturbosch.detekt.DetektCreateBaselineTask::class) {
    description = "Overrides current baseline."
    buildUponDefaultConfig.set(true)
    ignoreFailures.set(true)
    parallel.set(true)
    setSource(files(rootDir))
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    baseline.set(file("$rootDir/config/detekt/baseline.xml"))
    include("**/*.kt")
    include("**/*.kts")
    exclude("**/resources/**")
    exclude("**/build/**")
}

detekt {
    buildUponDefaultConfig = true
    config = files("$projectDir/config/detekt/detekt.yml")
}

subprojects {
    pluginManager.apply("io.gitlab.arturbosch.detekt")
    detekt {
        baseline = file("${rootProject.projectDir}/config/detekt/baseline.xml")
    }
}