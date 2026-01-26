plugins {
    alias(budgetplus.plugins.kotlin.multiplatform)
    alias(budgetplus.plugins.metro)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.firebase.firestore)
        }
    }
}