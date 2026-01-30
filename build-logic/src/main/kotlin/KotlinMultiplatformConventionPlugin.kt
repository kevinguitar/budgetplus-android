import com.android.build.api.dsl.CommonExtension
import common.implementation
import common.libs
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val appId: String by project
        val modulePath = project.path
            .drop(1)
            .split(':', '-')
            .joinToString(".")

        project.apply(plugin = project.libs.plugins.kotlin.multiplatform.get().pluginId)
        project.apply(plugin = project.libs.plugins.android.library.get().pluginId)
//        project.apply(plugin = project.libs.plugins.android.kotlin.multiplatform.library.get().pluginId)
        project.apply(plugin = project.libs.plugins.kotlin.serialization.get().pluginId)

        project.extensions.configure(CommonExtension::class.java) {
            namespace = "$appId.$modulePath"
            compileSdk = project.libs.versions.compileAndroidSdk.get().toInt()
            defaultConfig.minSdk = project.libs.versions.minAndroidSdk.get().toInt()

            val javaVersion = JavaVersion.toVersion(project.libs.versions.jvmTarget.get())
            compileOptions.sourceCompatibility = javaVersion
            compileOptions.targetCompatibility = javaVersion

            packaging.resources {
                excludes.add("META-INF/*.kotlin_module")
                excludes.add("META-INF/AL2.0")
                excludes.add("META-INF/LICENSE.md")
                excludes.add("META-INF/LICENSE-notice.md")
                excludes.add("META-INF/LGPL2.1")
                excludes.add("**/*.kotlin_metadata")
            }
        }

        project.extensions.configure<KotlinMultiplatformExtension> {
            /*@Suppress("UnstableApiUsage")
            androidLibrary {
                namespace = "$appId.$modulePath"
                compileSdk = project.libs.versions.compileAndroidSdk.get().toInt()
                minSdk = project.libs.versions.minAndroidSdk.get().toInt()
                compilerOptions {
                    jvmTarget.set(project.libs.versions.jvmTarget.map(JvmTarget::fromTarget))
                }
                androidResources.enable = true
            }*/

            applyDefaultHierarchyTemplate()

            androidTarget()

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

            jvmToolchain(project.libs.versions.jvmTarget.get().toInt())

            sourceSets.apply {
                commonMain.dependencies {
                    implementation(project.libs.kotlin.datetime)

                    if (project.path != ":core:common") {
                        implementation(project(":core:common"))
                    }
                }

                androidMain.dependencies {
                    implementation(project.libs.bundles.android)
                    implementation(project.libs.bundles.kmp)
                }

                androidUnitTest.dependencies {
                    implementation(kotlin("test"))
                    implementation(project.libs.bundles.test)

                    if (project.path != ":core:unit-test") {
                        implementation(project(":core:unit-test"))
                    }
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

        project.dependencies {
            val bomBundle = project.libs.bundles.bom.get()
            bomBundle.forEach { bom ->
                implementation(enforcedPlatform(bom))
            }
        }
    }
}