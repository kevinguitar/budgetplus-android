plugins {
    alias(budgetplus.plugins.kotlin.multiplatform)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.coroutines.test)
            implementation(libs.junit.compose)
        }
    }
}