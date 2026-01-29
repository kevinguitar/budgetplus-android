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
            // For displaying the color tone content preview
            implementation(projects.feature.categoryPills)
        }
        androidMain.dependencies {
            implementation(libs.compottie)
            implementation(libs.orchestra.colorpicker)
        }
    }
}