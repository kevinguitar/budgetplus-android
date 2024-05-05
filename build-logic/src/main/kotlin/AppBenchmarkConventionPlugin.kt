import com.android.build.api.dsl.TestExtension
import com.android.build.api.variant.TestAndroidComponentsExtension
import common.Constants
import common.compileOnly
import common.implementation
import common.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AppBenchmarkConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.apply(plugin = project.libs.plugins.android.test.get().pluginId)
        project.apply(plugin = project.libs.plugins.kotlin.android.get().pluginId)

        val appId: String by project
        val minAndroidSdk: String by project
        val androidSdk: String by project

        project.extensions.configure<TestExtension> {

            namespace = "$appId.benchmark"
            compileSdk = androidSdk.toInt()

            compileOptions {
                sourceCompatibility = Constants.javaVersion
                targetCompatibility = Constants.javaVersion
            }

            project.tasks.withType<KotlinCompile>().configureEach {
                compilerOptions {
                    jvmTarget.set(Constants.jvmTarget)
                }
            }

            defaultConfig {
                minSdk = minAndroidSdk.toInt()
                targetSdk = androidSdk.toInt()

                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }

            buildTypes {
                create("release") {
                    isMinifyEnabled = true
                    isShrinkResources = true
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                }

                // This benchmark buildType is used for benchmarking, and should function like your
                // release build (for example, with minification on). It's signed with a debug key
                // for easy local/CI testing.
                create("benchmark") {
                    initWith(getByName("release"))
                    signingConfig = getByName("debug").signingConfig
                    matchingFallbacks += listOf("release")
                }
            }

            targetProjectPath = ":app"

            @Suppress("UnstableApiUsage")
            experimentalProperties["android.experimental.self-instrumenting"] = true
        }

        project.extensions.configure<TestAndroidComponentsExtension> {
            beforeVariants(selector().all()) {
                it.enable = it.buildType == "benchmark"
            }
        }

        project.dependencies {
            implementation(project.libs.junit.android)
            implementation(project.libs.espresso)
            implementation(project.libs.test.uiautomator)
            implementation(project.libs.macro.benchmark)

            // Required for r8
            compileOnly(project.libs.google.errorprone)
        }
    }
}