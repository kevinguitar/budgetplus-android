import common.implementation
import common.libs
import dev.zacsweers.metro.gradle.MetroPluginExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

class MetroConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.apply(plugin = project.libs.plugins.metro.get().pluginId)

        project.configure<MetroPluginExtension> {
            contributesAsInject.set(true)
        }

        project.plugins.withType<ComposeConventionPlugin> {
            project.dependencies {
                implementation(project.libs.metrox.viewmodel.compose)
            }
        }
    }
}