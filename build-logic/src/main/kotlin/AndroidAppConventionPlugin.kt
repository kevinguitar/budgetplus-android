import com.android.build.api.dsl.ApplicationExtension
import common.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.provideDelegate
import kotlin.math.pow

class AndroidAppConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.apply(plugin = project.libs.plugins.android.application.get().pluginId)
        project.apply(plugin = project.libs.plugins.google.services.get().pluginId)
        project.apply(plugin = project.libs.plugins.firebase.crashlytics.get().pluginId)
        project.apply<KotlinAndroidConventionPlugin>()

        val appId: String by project
        val appVersion: String by project
        val androidSdk: String by project

        /**
         *  Major version * 10^6
         *  Minor version * 10^3
         *  Bugfix version * 10^0
         *
         *  "1.1.4"     to 1001004
         *  "3.100.1"   to 3100001
         *  "10.52.39"  to 10052039
         */
        val appVersionCode = appVersion
            .split(".")
            .reversed()
            .mapIndexed { index, num ->
                num.toInt() * 10.0.pow(index * VERSION_DIGIT).toInt()
            }
            .sum()

        project.extensions.configure<ApplicationExtension> {
            defaultConfig {
                applicationId = appId
                targetSdk = androidSdk.toInt()
                versionName = appVersion
                versionCode = appVersionCode

                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                vectorDrawables {
                    useSupportLibrary = true
                }
            }

            signingConfigs {
                create("release") {
                    storeFile = project.rootProject.file("misc/BudgetKey")
                    storePassword = "budget+"
                    keyAlias = "key0"
                    keyPassword = "budget+"
                }
            }

            buildTypes {
                getByName("release") {
                    isMinifyEnabled = true
                    isShrinkResources = true
                    signingConfig = signingConfigs.getByName("release")
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                }

                create("benchmark") {
                    signingConfig = signingConfigs.getByName("debug")
                    matchingFallbacks.add("release")
                    isDebuggable = false
                }
            }

            bundle {
                storeArchive.enable = false
            }

            androidResources {
                generateLocaleConfig = true
            }
        }
    }

    private companion object {
        const val VERSION_DIGIT = 3
    }
}