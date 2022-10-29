import com.android.build.api.dsl.TestExtension
import com.android.build.api.variant.TestAndroidComponentsExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.provideDelegate

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
                sourceCompatibility = JavaVersion.VERSION_1_8
                targetCompatibility = JavaVersion.VERSION_1_8
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
            add("implementation", libs.findLibrary("junit.android").get())
            add("implementation", libs.findLibrary("espresso").get())
            add("implementation", libs.findLibrary("test.uiautomator").get())
            add("implementation", libs.findLibrary("macro.benchmark").get())

            // Required for r8
            add("compileOnly", libs.findLibrary("google.errorprone").get())
        }
    }
}