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
        register("android.app") {
            id = "budgetplus.android.app"
            implementationClass = "AndroidAppConventionPlugin"
        }
        register("android.library") {
            id = "budgetplus.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("app.benchmark") {
            id = "budgetplus.app.benchmark"
            implementationClass = "AppBenchmarkConventionPlugin"
        }
        register("compose") {
            id = "budgetplus.compose"
            implementationClass = "ComposeConventionPlugin"
        }
        register("hilt") {
            id = "budgetplus.hilt"
            implementationClass = "HiltConventionPlugin"
        }
        register("kotlin.serialization") {
            id = "budgetplus.kotlin.serialization"
            implementationClass = "KotlinSerializationConventionPlugin"
        }
    }
}