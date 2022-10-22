ext {
    set("application_id", "com.kevlina.budgetplus")
    set("app_version", "1.1.11")
    set("min_android_sdk", 23)
    set("android_sdk", 33)
}

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    with(libs.plugins) {
        alias(android.application) apply false
        alias(android.test) apply false
        alias(kotlin.android) apply false
        alias(kotlin.kapt) apply false
        alias(kotlin.serialization) apply false
        alias(hilt.android) apply false
        alias(google.services) apply false
        alias(firebase.crashlytics) apply false
        alias(toml.checker)
        alias(toml.updater)
    }
}

// Composable metrics
// ./gradlew assembleRelease -Pandroidx.enableComposeCompilerReports=true --rerun-tasks
// https://github.com/androidx/androidx/blob/androidx-main/compose/compiler/design/compiler-metrics.md
// https://chris.banes.dev/composable-metrics/
subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            if (project.findProperty("androidx.enableComposeCompilerReports") == "true") {
                freeCompilerArgs = freeCompilerArgs + listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
                        project.buildDir.absolutePath + "/compose_metrics"
                )
                freeCompilerArgs = freeCompilerArgs + listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
                        project.buildDir.absolutePath + "/compose_metrics"
                )
            }
        }
    }
}

tasks.register("clean", Delete::class) { delete(rootProject.buildDir) }