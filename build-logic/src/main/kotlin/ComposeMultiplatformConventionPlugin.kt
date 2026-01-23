import common.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class ComposeMultiplatformConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.apply(plugin = project.libs.plugins.jetbrains.compose.get().pluginId)
        project.apply(plugin = project.libs.plugins.compose.compiler.get().pluginId)

        project.extensions.configure<KotlinMultiplatformExtension> {
            sourceSets {
                commonMain.dependencies {
                    implementation(project.libs.cmp.runtime)
                    implementation(project.libs.cmp.foundation)
                    implementation(project.libs.cmp.ui)
                    implementation(project.libs.cmp.resources)
                    implementation(project.libs.cmp.uiTooling)

                    implementation(project.libs.coil.compose)
                }

                commonTest.dependencies {
                    implementation(kotlin("test"))
                }
            }
        }
    }
}
