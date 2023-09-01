package common

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *>,
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
            resources.excludes.add("META-INF/*")
        }

        tasks.withType<KotlinCompile>().configureEach {
            kotlinOptions {
                // Treat all Kotlin warnings as errors (disabled by default)
                // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
                val warningsAsErrors: String? by project
                allWarningsAsErrors = warningsAsErrors.toBoolean()

                freeCompilerArgs = freeCompilerArgs + listOf(
                    "-opt-in=kotlin.RequiresOptIn",
                    "-opt-in=kotlin.contracts.ExperimentalContracts",
                    "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                )

                jvmTarget = Constants.javaVersion.toString()
            }
        }
    }

    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    dependencies {
        add("implementation", libs.findLibrary("android.core").get())
        add("implementation", libs.findLibrary("coroutines").get())
        add("implementation", libs.findLibrary("timber").get())
        add("implementation", libs.findBundle("test").get())
        add("coreLibraryDesugaring", libs.findLibrary("desugar").get())

        if (path != ":core:common") {
            add("implementation", project(":core:common"))
        }
    }
}
