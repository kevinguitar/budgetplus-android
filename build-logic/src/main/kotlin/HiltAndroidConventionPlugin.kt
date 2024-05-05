import common.implementation
import common.ksp
import common.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class HiltAndroidConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.apply(plugin = project.libs.plugins.hilt.android.get().pluginId)
        project.apply(plugin = project.libs.plugins.google.ksp.get().pluginId)

        project.dependencies {
            implementation(project.libs.navigation.hilt)
            implementation(project.libs.hilt.android)
            ksp(project.libs.hilt.compiler)
        }
    }
}