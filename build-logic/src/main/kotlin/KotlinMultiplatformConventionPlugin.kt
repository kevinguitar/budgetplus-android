import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import common.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

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

            applyDefaultHierarchyTemplate()

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

                androidUnitTest.dependencies {
                    implementation(kotlin("test"))
                    implementation(project.libs.bundles.test)
                }

                val androidTestFixtures = create("androidTestFixtures") {
                    dependsOn(androidMain.get())
                    dependencies {
                        //TODO: Are they needed?
                        implementation(kotlin("stdlib"))
                        implementation(project.libs.android.activity)
                    }
                }
                androidUnitTest.get().dependsOn(androidTestFixtures)
            }

            targets.configureEach {
                if (platformType == KotlinPlatformType.androidJvm) {
                    compilations.configureEach {
                        if (name.endsWith("UnitTest")) {
                            // Explicitly associate with the main compilation to access production code
                            // This ensures fixtures and tests can see classes from commonMain and androidMain
                            val mainCompilationName = name.removeSuffix("UnitTest")
                            val mainCompilation = target.compilations.findByName(mainCompilationName)
                            if (mainCompilation != null) {
                                associateWith(mainCompilation)
                            }
                        }
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
    }
}