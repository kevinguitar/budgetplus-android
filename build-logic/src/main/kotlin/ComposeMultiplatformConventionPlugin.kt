import common.libs
import common.testFixturesImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class ComposeMultiplatformConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.apply(plugin = project.libs.plugins.jetbrains.compose.get().pluginId)
        project.apply<SetupComposeCompiler>()

        project.extensions.configure<KotlinMultiplatformExtension> {
            sourceSets {
                commonMain.dependencies {
                    implementation(project.libs.bundles.compose)
                    implementation(project.libs.coil.compose)
                }
                commonTest.dependencies {
                    implementation(kotlin("test"))
                }
                androidMain.dependencies {
                    implementation(project.libs.android.activity.compose)
                }
            }
        }

        project.dependencies {
            testFixturesImplementation(project.libs.cmp.runtime)
            testFixturesImplementation(project.libs.junit.compose)
        }
    }
}
