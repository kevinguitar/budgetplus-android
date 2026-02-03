import common.implementation

plugins {
    alias(budgetplus.plugins.android.app)
    alias(budgetplus.plugins.metro)
    alias(budgetplus.plugins.compose)
}

dependencies {
    implementation(enforcedPlatform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    implementation(libs.navigation3.ui)
    implementation(libs.navigation3.viewmodel)
    implementation(libs.profile.installer)
    implementation(libs.google.ads)
    implementation(libs.compottie)
    implementation(libs.coil.okhttp)

    implementation(projects.core.ads)
    implementation(projects.core.billing)
    implementation(projects.core.data)
    implementation(projects.core.impl)
    implementation(projects.core.inappUpdate)
    implementation(projects.core.notification)
    implementation(projects.core.settingsApi)
    implementation(projects.core.ui)

    implementation(projects.feature.addRecord)
    implementation(projects.feature.auth)
    implementation(projects.feature.categoryPills)
    implementation(projects.feature.colorTonePicker)
    implementation(projects.feature.currencyPicker)
    implementation(projects.feature.editCategory)
    implementation(projects.feature.overview)
    implementation(projects.feature.recordCard)
    implementation(projects.feature.records)
    implementation(projects.feature.search)
    implementation(projects.feature.settings)
    implementation(projects.feature.speakToRecord)
    implementation(projects.feature.unlockPremium)
    implementation(projects.feature.welcome)

    implementation(projects.composeApp)
}