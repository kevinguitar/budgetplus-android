plugins {
    alias(budgetplus.plugins.kotlin.multiplatform)
    alias(budgetplus.plugins.metro)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.data)
        }
        androidMain.dependencies {
            api(libs.firebase.messaging)
        }
    }
}