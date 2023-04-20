import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

class HiltAndroidConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply {
            apply("org.jetbrains.kotlin.kapt")
            apply("com.google.dagger.hilt.android")
        }

        extensions.configure<KaptExtension> {
            correctErrorTypes = true
            // This is experimental feature but it can improve build time
            // If you faced issue with annotation processing please report to http://kotl.in/issue
            useBuildCache = true
            // If you get error during annotation processing you will see link to
            // original Kotlin file instead of generated Java stub
            mapDiagnosticLocations = true
        }

        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        dependencies {
            add("implementation", libs.findLibrary("navigation.hilt").get())
            add("implementation", libs.findLibrary("hilt.android").get())
            add("kapt", libs.findLibrary("hilt.compiler").get())
        }
    }
}