plugins {
    alias(budgetplus.plugins.kotlin.multiplatform)
    alias(budgetplus.plugins.compose.multiplatform)
    alias(budgetplus.plugins.metro)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.data)
            implementation(projects.core.ui)
            implementation(projects.feature.categoryPills)
            implementation(projects.feature.recordCard)
        }
        androidMain.dependencies {
            implementation(libs.firebase.firestore)
        }
        androidUnitTest.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.data)
        }
    }
}