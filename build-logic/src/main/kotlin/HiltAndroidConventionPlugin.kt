import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class HiltAndroidConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply {
            apply("org.jetbrains.kotlin.kapt")
            apply("com.google.dagger.hilt.android")
        }

        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        dependencies {
            add("implementation", libs.findLibrary("navigation.hilt").get())
            add("implementation", libs.findLibrary("hilt.android").get())
            add("kapt", libs.findLibrary("hilt.compiler").get())
        }
    }
}