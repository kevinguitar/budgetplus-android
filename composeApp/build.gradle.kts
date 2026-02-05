plugins {
    alias(budgetplus.plugins.kotlin.multiplatform)
    alias(budgetplus.plugins.compose.multiplatform)
    alias(budgetplus.plugins.metro)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.navigation3.ui)
            implementation(libs.navigation3.viewmodel)
            implementation(libs.compottie)

            api(projects.core.ads)
            api(projects.core.billing)
            api(projects.core.data)
            api(projects.core.inappUpdate)
            api(projects.core.settingsApi)
            api(projects.core.ui)

            api(projects.feature.addRecord)
            api(projects.feature.auth)
            api(projects.feature.categoryPills)
            api(projects.feature.colorTonePicker)
            api(projects.feature.currencyPicker)
            api(projects.feature.editCategory)
            api(projects.feature.overview)
            api(projects.feature.recordCard)
            api(projects.feature.records)
            api(projects.feature.search)
            api(projects.feature.settings)
            api(projects.feature.speakToRecord)
            api(projects.feature.unlockPremium)
            api(projects.feature.welcome)
        }
        androidMain.dependencies {
            implementation(libs.google.ads)
        }
    }
}