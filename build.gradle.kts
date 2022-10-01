buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.14")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.2")
    }
}

plugins {
    // Version of Android Gradle Plugin
    // https://maven.google.com/web/index.html?q=gradle#com.android.tools.build:gradle
    val agpVersion = "7.3.0"

    // Kotlin
    // Full release notes - https://github.com/JetBrains/kotlin/releases
    // release - https://kotlinlang.org/docs/releases.html#release-details
    // eap - https://kotlinlang.org/docs/eap.html#build-details
    // Before updating version of Kotlin always check version supported by Compose
    // https://developer.android.com/jetpack/compose/interop/adding#anchor
    val kotlinVersion = "1.7.10"

    id("com.android.application") version agpVersion apply false
    id("com.android.test") version agpVersion apply false
    id("org.jetbrains.kotlin.android") version kotlinVersion apply false
    id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion apply false
    id("org.jetbrains.kotlin.jvm") version kotlinVersion apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
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