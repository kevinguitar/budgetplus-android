import com.android.build.api.dsl.ApplicationExtension
import common.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.provideDelegate
import kotlin.math.pow

class AndroidAppConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.android.application")
            apply("org.jetbrains.kotlin.android")
            apply("com.google.gms.google-services")
            apply("com.google.firebase.crashlytics")
        }

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

        extensions.configure<ApplicationExtension> {
            configureKotlinAndroid(commonExtension = this, ns = appId)

            defaultConfig {
                applicationId = appId
                targetSdk = androidSdk.toInt()
                versionName = appVersion
                versionCode = appVersionCode

                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                vectorDrawables {
                    useSupportLibrary = true
                }

                resourceConfigurations.addAll(
                    listOf("en", "zh-rCN", "zh-rTW", "ja-rJP")
                )
            }

            signingConfigs {
                create("release") {
                    storeFile = rootProject.file("misc/BudgetKey")
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
        }
    }

    private companion object {
        const val VERSION_DIGIT = 3
    }
}