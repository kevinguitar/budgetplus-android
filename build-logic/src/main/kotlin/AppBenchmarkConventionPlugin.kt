import com.android.build.api.dsl.TestExtension
import com.android.build.api.variant.TestAndroidComponentsExtension
import common.Constants
import common.compileOnly
import common.implementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
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
                kotlinOptions {
                    jvmTarget = Constants.javaVersion.toString()
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
                    isDebuggable = true
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

        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

        dependencies {
            // https://github.com/gradle/gradle/issues/15383
            implementation(libs.findLibrary("junit.android").get())
            implementation(libs.findLibrary("espresso").get())
            implementation(libs.findLibrary("test.uiautomator").get())
            implementation(libs.findLibrary("macro.benchmark").get())

            // Required for r8
            compileOnly(libs.findLibrary("google.errorprone").get())
        }
    }
}