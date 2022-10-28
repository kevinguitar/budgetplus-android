import com.android.build.api.dsl.ApplicationExtension
import common.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.provideDelegate

class AndroidAppConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.android.application")
            apply("org.jetbrains.kotlin.android")
        }

        val appId: String by project
        configureKotlinAndroid(
            commonExtension = extensions.getByType<ApplicationExtension>(),
            ns = appId
        )
    }
}