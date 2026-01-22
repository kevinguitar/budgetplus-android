plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.firebase.crashlytics.gradle)
    compileOnly(libs.build.android.gradle.plugin)
    compileOnly(libs.build.compose.gradle.plugin)
    compileOnly(libs.build.kotlin.gradle.plugin)
    compileOnly(libs.build.metro.gradle.plugin)
    compileOnly(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

gradlePlugin {
    plugins {
        register("android.app") {
            id = "budgetplus.android.app"
            implementationClass = "BudgetPlusAppConventionPlugin"
        }
        register("insider.app") {
            id = "budgetplus.insider.app"
            implementationClass = "InsiderAppConventionPlugin"
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
        register("metro") {
            id = "budgetplus.metro"
            implementationClass = "MetroConventionPlugin"
        }
        register("kotlin.serialization") {
            id = "budgetplus.kotlin.serialization"
            implementationClass = "KotlinSerializationConventionPlugin"
        }
        register("kotlin.multiplatform") {
            id = "budgetplus.kotlin.multiplatform"
            implementationClass = "MultiplatformConventionPlugin"
        }
    }
}