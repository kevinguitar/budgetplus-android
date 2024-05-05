package common

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//TODO: convert to plugin
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    ns: String? = null,
) {
    val appId: String by project
    val minAndroidSdk: String by project
    val androidSdk: String by project

    commonExtension.apply {
        val modulePath = this@configureKotlinAndroid.path
            .drop(1)
            .split(':', '-')
            .joinToString(".")

        namespace = ns ?: "$appId.$modulePath"
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

    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    dependencies {
        implementation(libs.findLibrary("android.core").get())
        implementation(libs.findLibrary("coroutines").get())
        implementation(libs.findLibrary("timber").get())
        implementation(libs.findBundle("test").get())
        coreLibraryDesugaring(libs.findLibrary("desugar").get())

        if (path != ":core:common") {
            implementation(project(":core:common"))
        }
    }
}
