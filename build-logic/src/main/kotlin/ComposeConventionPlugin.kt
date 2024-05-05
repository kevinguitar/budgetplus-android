import com.android.build.api.dsl.CommonExtension
import common.implementation
import common.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class ComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

        project.extensions.configure(CommonExtension::class.java) {
            buildFeatures {
                compose = true
            }

            tasks.withType<KotlinCompile>().configureEach {
                compilerOptions {
                    freeCompilerArgs.addAll(
                        "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
                        "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                        "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi",
                    )
                    freeCompilerArgs.addAll(buildStabilityConfiguration())
                    freeCompilerArgs.addAll(buildStrongSkippingMode())
                    freeCompilerArgs.addAll(buildComposeMetricsParameters())
                }
            }
        }

        dependencies {
            project.libs.compose.bom.get()
            implementation(platform(libs.findLibrary("compose.bom").get()))
            implementation(libs.findBundle("compose").get())
        }
    }
}

private fun Project.buildStabilityConfiguration(): List<String> = listOf(
    // https://developer.android.com/jetpack/compose/performance/stability/fix#configuration-file
    "-P",
    "plugin:org.jetbrains.kotlin.compose.compiler.gradle:stabilityConfigurationFile=" +
        rootProject.file("misc/compose_compiler_config.conf").absolutePath
)

private fun Project.buildStrongSkippingMode(): List<String> = listOf(
    // Enable the strong skipping mode
    // https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/compiler/design/strong-skipping.md#other-gradle-projects
    "-P",
    "plugin:org.jetbrains.kotlin.compose.compiler.gradle:enableStrongSkippingMode=true"
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
            "plugin:org.jetbrains.kotlin.compose.compiler.gradle:metricsDestination=" + metricsFolder.absolutePath
        )
    }

    val enableReportsProvider = project.providers.gradleProperty("enableComposeCompilerReports")
    val enableReports = (enableReportsProvider.orNull == "true")
    if (enableReports) {
        val reportsFolder = project.layout.buildDirectory.dir("compose-reports").get().asFile
        metricParameters.add("-P")
        metricParameters.add(
            "plugin:org.jetbrains.kotlin.compose.compiler.gradle:reportsDestination=" + reportsFolder.absolutePath
        )
    }
    return metricParameters.toList()
}
