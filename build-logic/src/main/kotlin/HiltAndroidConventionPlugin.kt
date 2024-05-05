import common.implementation
import common.ksp
import common.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class HiltAndroidConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply {
            apply("com.google.dagger.hilt.android")
            apply("com.google.devtools.ksp")
        }

        dependencies {
            implementation(libs.navigation.hilt)
            implementation(libs.hilt.android)
            ksp(libs.hilt.compiler)
        }
    }
}