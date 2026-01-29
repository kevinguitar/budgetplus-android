plugins {
    alias(budgetplus.plugins.kotlin.multiplatform)
}

kotlin {
    sourceSets {
        androidTestFixtures.dependencies {
            implementation(libs.coroutines.test)
            implementation(libs.junit.compose)
        }
    }
}