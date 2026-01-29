plugins {
    alias(budgetplus.plugins.kotlin.multiplatform)
    alias(budgetplus.plugins.compose.multiplatform)
    alias(budgetplus.plugins.metro)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.ads)
            implementation(projects.core.data)
            implementation(projects.core.inappReview)
            implementation(projects.core.ui)
            implementation(projects.feature.categoryPills)
            implementation(projects.feature.speakToRecord)
        }
        androidMain.dependencies {
            implementation(libs.lottie.compose)
            implementation(libs.exp4j)
        }
    }
}