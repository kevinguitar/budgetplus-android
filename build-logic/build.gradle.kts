plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApp") {
            id = "budgetplus.android.app"
            implementationClass = "AndroidAppConventionPlugin"
        }
        register("androidLibrary") {
            id = "budgetplus.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("hiltAndroid") {
            id = "budgetplus.hilt.android"
            implementationClass = "HiltAndroidConventionPlugin"
        }
        register("composeApp") {
            id = "budgetplus.compose.app"
            implementationClass = "ComposeAppConventionPlugin"
        }
        register("composeLibrary") {
            id = "budgetplus.compose.library"
            implementationClass = "ComposeLibraryConventionPlugin"
        }
    }
}