import com.android.build.api.dsl.TestExtension
import com.android.build.api.variant.TestAndroidComponentsExtension
import common.Constants
import common.compileOnly
import common.implementation
import common.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AppBenchmarkConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.android.test")
            apply("org.jetbrains.kotlin.android")
        }

        val appId: String by project
        val minAndroidSdk: String by project
        val androidSdk: String by project

        extensions.configure<TestExtension> {

            namespace = "$appId.benchmark"
            compileSdk = androidSdk.toInt()

            compileOptions {
                sourceCompatibility = Constants.javaVersion
                targetCompatibility = Constants.javaVersion
            }

            tasks.withType<KotlinCompile>().configureEach {
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
                // release build (for example, with minification on). It"s signed with a debug key
                // for easy local/CI testing.
                create("benchmark") {
                    initWith(getByName("release"))
                    signingConfig = getByName("debug").signingConfig
                    matchingFallbacks += listOf("release")
                }
            }

            targetProjectPath = ":app"
            experimentalProperties["android.experimental.self-instrumenting"] = true
        }

        extensions.configure<TestAndroidComponentsExtension> {
            beforeVariants(selector().all()) {
                it.enable = it.buildType == "benchmark"
            }
        }

        dependencies {
            implementation(libs.junit.android)
            implementation(libs.espresso)
            implementation(libs.test.uiautomator)
            implementation(libs.macro.benchmark)

            // Required for r8
            compileOnly(libs.google.errorprone)
        }
    }
}