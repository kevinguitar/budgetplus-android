plugins {
    alias(budgetplus.plugins.kotlin.multiplatform)
    alias(budgetplus.plugins.metro)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.serialization)
        }
        androidMain.dependencies {
            implementation(libs.firebase.firestore)
        }
    }
}