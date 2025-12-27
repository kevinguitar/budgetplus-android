import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.TestedExtension
import common.implementation
import common.libs
import common.testFixturesImplementation
import common.testImplementation
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
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
                val javaVersion = JavaVersion.toVersion(project.libs.versions.jvmTarget.get())
                sourceCompatibility = javaVersion
                targetCompatibility = javaVersion
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
                    jvmTarget.set(project.libs.versions.jvmTarget.map(JvmTarget::fromTarget))
                }
            }
        }

        // Enable test fixtures for all modules
        project.extensions.configure<TestedExtension> {
            @Suppress("UnstableApiUsage")
            testFixtures.enable = true
        }

        project.dependencies {
            implementation(project.libs.bundles.android)
            implementation(project.libs.timber)
            testImplementation(project.libs.bundles.test)

            if (project.path != ":core:common") {
                implementation(project(":core:common"))
                testImplementation(testFixtures(project(":core:common")))
            }

            testImplementation(testFixtures(project(":core:unit-test")))
            testFixturesImplementation(project.libs.coroutines.android)
        }
    }
}
