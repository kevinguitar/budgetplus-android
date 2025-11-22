import common.implementation
import common.ksp
import common.libs
import dev.zacsweers.metro.gradle.MetroPluginExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

//TODO: Rename to metro
class HiltConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.apply(plugin = project.libs.plugins.hilt.android.get().pluginId)
        project.apply(plugin = project.libs.plugins.google.ksp.get().pluginId)
        project.apply(plugin = project.libs.plugins.metro.get().pluginId)

        project.configure<MetroPluginExtension> {
            contributesAsInject.set(true)
            transformProvidersToPrivate.set(false) //TODO: For Hilt to compile
            interop {
                includeDagger(includeJavax = true)
            }
        }

        project.dependencies {
            implementation(project.libs.navigation.hilt)
            implementation(project.libs.hilt.android)
            ksp(project.libs.hilt.compiler)
        }
    }
}