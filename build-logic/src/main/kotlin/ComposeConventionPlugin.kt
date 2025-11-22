import com.android.build.api.dsl.CommonExtension
import common.debugImplementation
import common.implementation
import common.libs
import common.testFixturesImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class ComposeConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.apply(plugin = project.libs.plugins.compose.compiler.get().pluginId)

        project.extensions.configure(CommonExtension::class.java) {
            buildFeatures {
                compose = true
            }

            project.tasks.withType<KotlinCompile>().configureEach {
                compilerOptions {
                    optIn.addAll(
                        "androidx.compose.ui.ExperimentalComposeUiApi",
                        "androidx.compose.foundation.ExperimentalFoundationApi",
                        "androidx.compose.foundation.layout.ExperimentalLayoutApi",
                    )
                }
            }
        }

        project.extensions.configure<ComposeCompilerGradlePluginExtension> {
            // https://developer.android.com/jetpack/compose/performance/stability/fix#configuration-file
            stabilityConfigurationFiles.add(
                project.rootProject.layout.projectDirectory.file("compose_compiler_config.conf")
            )

            // Composable metrics
            // ./gradlew assembleRelease -PenableComposeCompilerMetrics=true --rerun-tasks
            // ./gradlew assembleRelease -PenableComposeCompilerReports=true --rerun-tasks
            // https://github.com/androidx/androidx/blob/androidx-main/compose/compiler/design/compiler-metrics.md
            // https://chris.banes.dev/composable-metrics/
            val enableMetricsProvider = project.providers.gradleProperty("enableComposeCompilerMetrics")
            val enableMetrics = enableMetricsProvider.orNull == "true"
            if (enableMetrics) {
                val metricsFolder = project.layout.buildDirectory.dir("compose-metrics")
                metricsDestination.set(metricsFolder)
            }

            val enableReportsProvider = project.providers.gradleProperty("enableComposeCompilerReports")
            val enableReports = (enableReportsProvider.orNull == "true")
            if (enableReports) {
                val reportsFolder = project.layout.buildDirectory.dir("compose-reports")
                reportsDestination.set(reportsFolder)
            }
        }

        project.dependencies {
            implementation(platform(project.libs.compose.bom))
            implementation(project.libs.bundles.compose)
            implementation(project.libs.android.activity)
            debugImplementation(project.libs.compose.tooling)

            testFixturesImplementation(platform(project.libs.compose.bom))
            testFixturesImplementation(project.libs.compose.runtime)
            testFixturesImplementation(project.libs.junit.compose)
        }
    }
}
