import com.android.build.api.dsl.CommonExtension
import common.Constants
import common.coreLibraryDesugaring
import common.implementation
import common.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class KotlinAndroidConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.apply(plugin = project.libs.plugins.kotlin.android.get().pluginId)

        val appId: String by project
        val minAndroidSdk: String by project
        val androidSdk: String by project

        project.extensions.configure(CommonExtension::class.java) {
            val modulePath = project.path
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

            project.tasks.withType<KotlinCompile>().configureEach {
                compilerOptions {
                    allWarningsAsErrors.set(true)

                    freeCompilerArgs.addAll(
                        "-Xcontext-receivers",
                        "-opt-in=kotlin.RequiresOptIn",
                        "-opt-in=kotlin.contracts.ExperimentalContracts",
                        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                    )

                    jvmTarget.set(Constants.jvmTarget)
                }
            }
        }

        project.dependencies {
            implementation(project.libs.android.core)
            implementation(project.libs.coroutines)
            implementation(project.libs.timber)
            implementation(project.libs.bundles.test)
            coreLibraryDesugaring(project.libs.desugar)

            if (project.path != ":core:common") {
                implementation(project(":core:common"))
            }
        }
    }
}
