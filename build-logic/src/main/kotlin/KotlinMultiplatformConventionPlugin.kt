import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import common.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val appId: String by project
        val modulePath = project.path
            .drop(1)
            .split(':', '-')
            .joinToString(".")

        project.apply(plugin = project.libs.plugins.kotlin.multiplatform.get().pluginId)
        project.apply(plugin = project.libs.plugins.android.kotlin.multiplatform.library.get().pluginId)
        project.apply(plugin = project.libs.plugins.kotlin.serialization.get().pluginId)

        project.extensions.configure<KotlinMultiplatformExtension> {
            extensions.configure<KotlinMultiplatformAndroidLibraryTarget> {
                namespace = "$appId.$modulePath"
                compileSdk = project.libs.versions.compileAndroidSdk.get().toInt()
                minSdk = project.libs.versions.minAndroidSdk.get().toInt()
                compilerOptions {
                    jvmTarget.set(project.libs.versions.jvmTarget.map(JvmTarget::fromTarget))
                }
                androidResources.enable = true
            }

            listOf(
                iosX64(),
                iosArm64(),
                iosSimulatorArm64()
            ).forEach { iosTarget ->
                iosTarget.binaries.framework {
                    baseName = modulePath.replaceFirstChar { it.uppercase() }
                    isStatic = true
                }
            }

            sourceSets.apply {
                commonMain.dependencies {
                    implementation(project.libs.kotlin.datetime)
                    implementation(project.libs.kotlin.serialization)

                    if (project.path != ":core:common") {
                        implementation(project(":core:common"))
                    }
                }

                androidMain.dependencies {
                    val bomBundle = project.libs.bundles.bom.get()
                    bomBundle.forEach { bom ->
                        project.dependencies.enforcedPlatform(bom)
                    }

                    implementation(project.libs.bundles.android)
                    implementation(project.libs.timber)
                }

                // For migration, ideally should be commonUnitTest
                androidUnitTest.dependencies {
                    implementation(project.libs.bundles.test)
                }
            }

            compilerOptions {
                allWarningsAsErrors.set(true)
                freeCompilerArgs.addAll(
                    "-Xcontext-parameters",
                    "-Xexplicit-backing-fields",
                    "-Xannotation-default-target=param-property"
                )
                optIn.addAll(
                    "kotlin.contracts.ExperimentalContracts",
                    "kotlinx.coroutines.ExperimentalCoroutinesApi",
                    "kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi",
                    "kotlinx.coroutines.FlowPreview"
                )
            }
        }
    }
}