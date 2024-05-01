import com.android.build.gradle.LibraryExtension
import common.configureComposeAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class ComposeLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.library")
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
        configureComposeAndroid(extensions.getByType<LibraryExtension>())
    }
}