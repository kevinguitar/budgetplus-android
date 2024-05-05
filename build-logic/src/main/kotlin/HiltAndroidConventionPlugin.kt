import common.implementation
import common.ksp
import common.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class HiltAndroidConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        apply(plugin = libs.plugins.hilt.android.get().pluginId)
        apply(plugin = libs.plugins.google.ksp.get().pluginId)

        dependencies {
            implementation(libs.navigation.hilt)
            implementation(libs.hilt.android)
            ksp(libs.hilt.compiler)
        }
    }
}