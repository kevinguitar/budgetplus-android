import com.android.build.gradle.LibraryExtension
import common.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.android.library")
            apply("org.jetbrains.kotlin.android")
        }

        configureKotlinAndroid(extensions.getByType<LibraryExtension>())

        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        dependencies {
            add("api", libs.findLibrary("android.core").get())
            add("api", libs.findLibrary("coroutines").get())
            add("api", libs.findLibrary("timber").get())
        }
    }
}