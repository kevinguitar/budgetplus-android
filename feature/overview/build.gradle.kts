plugins {
    alias(budgetplus.plugins.kotlin.multiplatform)
    alias(budgetplus.plugins.compose.multiplatform)
    alias(budgetplus.plugins.metro)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.data)
            implementation(projects.core.settingsApi)
            implementation(projects.core.ui)
            implementation(projects.feature.recordCard)

            implementation(libs.permissions.compose)
            implementation(libs.permissions.storage)
        }
        androidMain.dependencies {
            //TODO: Eh this lacks iOS support
            implementation(libs.kotlin.csv)
        }
    }
}