plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
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
        register("appBenchmark") {
            id = "budgetplus.app.benchmark"
            implementationClass = "AppBenchmarkConventionPlugin"
        }
        register("compose") {
            id = "budgetplus.compose"
            implementationClass = "ComposeConventionPlugin"
        }
        register("hiltAndroid") {
            id = "budgetplus.hilt.android"
            implementationClass = "HiltAndroidConventionPlugin"
        }
        register("kotlinSerialization") {
            id = "budgetplus.kotlin.serialization"
            implementationClass = "KotlinSerializationConventionPlugin"
        }
    }
}