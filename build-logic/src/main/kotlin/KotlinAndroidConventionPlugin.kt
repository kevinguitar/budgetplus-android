import com.android.build.api.dsl.CommonExtension
import common.Constants
import common.coreLibraryDesugaring
import common.implementation
import common.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class KotlinAndroidConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val appId: String by project
        val minAndroidSdk: String by project
        val androidSdk: String by project

        project.extensions.configure(CommonExtension::class.java) {
            val modulePath = this@with.path
                .drop(1)
                .split(':', '-')
                .joinToString(".")

            namespace = "$appId.$modulePath"
            compileSdk = androidSdk.toInt()

            defaultConfig {
                minSdk = minAndroidSdk.toInt()
            }

            compileOptions {
                sourceCompatibility = Constants.javaVersion
                targetCompatibility = Constants.javaVersion
                isCoreLibraryDesugaringEnabled = true
            }

            packaging {
                resources {
                    excludes.add("META-INF/*.kotlin_module")
                    excludes.add("META-INF/AL2.0")
                    excludes.add("META-INF/LICENSE.md")
                    excludes.add("META-INF/LICENSE-notice.md")
                    excludes.add("META-INF/LGPL2.1")
                    excludes.add("**/*.kotlin_metadata")
                }
            }

            tasks.withType<KotlinCompile>().configureEach {
                compilerOptions {
                    // Treat all Kotlin warnings as errors (disabled by default)
                    // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
                    val warningsAsErrors: String? by project
                    allWarningsAsErrors.set(warningsAsErrors.toBoolean())

                    freeCompilerArgs.addAll(
                        "-opt-in=kotlin.RequiresOptIn",
                        "-opt-in=kotlin.contracts.ExperimentalContracts",
                        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                    )

                    jvmTarget.set(Constants.jvmTarget)
                }
            }
        }

        dependencies {
            implementation(libs.android.core)
            implementation(libs.coroutines)
            implementation(libs.timber)
            implementation(libs.bundles.test)
            coreLibraryDesugaring(libs.desugar)

            if (path != ":core:common") {
                implementation(project(":core:common"))
            }
        }
    }
}
