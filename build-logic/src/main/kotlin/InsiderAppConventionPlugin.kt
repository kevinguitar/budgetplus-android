import com.android.build.api.dsl.ApplicationExtension
import common.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.provideDelegate

class InsiderAppConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.apply(plugin = project.libs.plugins.android.application.get().pluginId)
        project.apply(plugin = project.libs.plugins.google.services.get().pluginId)
        project.apply(plugin = project.libs.plugins.firebase.crashlytics.get().pluginId)
        project.apply<KotlinAndroidConventionPlugin>()

        val appId: String by project
        val androidSdk: String by project

        project.extensions.configure<ApplicationExtension> {
            defaultConfig {
                applicationId = appId
                targetSdk = androidSdk.toInt()
                versionName = "1.0.0"
                versionCode = 1

                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                vectorDrawables {
                    useSupportLibrary = true
                }
            }

            bundle {
                storeArchive.enable = false
            }
        }
    }
}