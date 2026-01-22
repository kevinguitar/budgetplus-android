import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import common.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val appId: String by project
        val minAndroidSdk: String by project
        val androidSdk: String by project

        project.apply(plugin = project.libs.plugins.kotlin.multiplatform.get().pluginId)
        project.apply(plugin = project.libs.plugins.android.kotlin.multiplatform.library.get().pluginId)

        project.extensions.configure<KotlinMultiplatformExtension> {
            extensions.configure<KotlinMultiplatformAndroidLibraryTarget> {
                val modulePath = project.path
                    .drop(1)
                    .split(':', '-')
                    .joinToString(".")

                namespace = "$appId.$modulePath"
                compileSdk = androidSdk.toInt()
                minSdk = minAndroidSdk.toInt()

                withDeviceTestBuilder {
                    sourceSetTreeName = "test"
                }.configure {
                    instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
            }

            listOf(
                iosX64(),
                iosArm64(),
                iosSimulatorArm64()
            ).forEach { iosTarget ->
                iosTarget.binaries.framework {
                    baseName = "shared"
                    isStatic = true
                }
            }

            sourceSets.apply {
                commonMain.dependencies {
                    implementation(project.libs.kotlin.datetime)
                }

                androidMain.dependencies {

                }
            }
        }
    }
}