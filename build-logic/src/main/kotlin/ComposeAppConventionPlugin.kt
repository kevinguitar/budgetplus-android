import com.android.build.api.dsl.ApplicationExtension
import common.configureComposeAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class ComposeAppConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.application")
        configureComposeAndroid(extensions.getByType<ApplicationExtension>())
    }
}