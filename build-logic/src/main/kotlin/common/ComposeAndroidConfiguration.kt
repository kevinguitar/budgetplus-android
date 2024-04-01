package common

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureComposeAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = libs.findVersion("compose.compiler").get().toString()
        }

        tasks.withType<KotlinCompile>().configureEach {
            kotlinOptions {
                freeCompilerArgs += listOf(
                    "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
                    "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                    "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi",
                ) +
                    buildStabilityConfiguration() +
                    buildStrongSkippingMode() +
                    buildComposeMetricsParameters()
            }
        }
    }

    dependencies {
        add("implementation", platform(libs.findLibrary("compose.bom").get()))
        add("implementation", libs.findBundle("compose").get())
    }
}

private fun Project.buildStabilityConfiguration(): List<String> = listOf(
    // https://developer.android.com/jetpack/compose/performance/stability/fix#configuration-file
    "-P",
    "plugin:androidx.compose.compiler.plugins.kotlin:stabilityConfigurationPath=" +
        rootProject.file("misc/compose_compiler_config.conf").absolutePath
)

private fun Project.buildStrongSkippingMode(): List<String> = listOf(
    // Enable the strong skipping mode
    // https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/compiler/design/strong-skipping.md#other-gradle-projects
    "-P",
    "plugin:androidx.compose.compiler.plugins.kotlin:experimentalStrongSkipping=true"
)

// Composable metrics
// ./gradlew assembleRelease -PenableComposeCompilerMetrics=true --rerun-tasks
// ./gradlew assembleRelease -PenableComposeCompilerReports=true --rerun-tasks
// https://github.com/androidx/androidx/blob/androidx-main/compose/compiler/design/compiler-metrics.md
// https://chris.banes.dev/composable-metrics/
private fun Project.buildComposeMetricsParameters(): List<String> {
    val metricParameters = mutableListOf<String>()
    val enableMetricsProvider = project.providers.gradleProperty("enableComposeCompilerMetrics")
    val enableMetrics = enableMetricsProvider.orNull == "true"
    if (enableMetrics) {
        val metricsFolder = project.layout.buildDirectory.dir("compose-metrics").get().asFile
        metricParameters.add("-P")
        metricParameters.add(
            "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" + metricsFolder.absolutePath
        )
    }

    val enableReportsProvider = project.providers.gradleProperty("enableComposeCompilerReports")
    val enableReports = (enableReportsProvider.orNull == "true")
    if (enableReports) {
        val reportsFolder = project.layout.buildDirectory.dir("compose-reports").get().asFile
        metricParameters.add("-P")
        metricParameters.add(
            "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" + reportsFolder.absolutePath
        )
    }
    return metricParameters.toList()
}
