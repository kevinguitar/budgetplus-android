plugins {
    id("budgetplus.android.app")
    id("budgetplus.hilt.android")
    id("budgetplus.compose.app")
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    implementation(libs.navigation.compose)
    implementation(libs.google.ads)
    implementation(libs.profile.installer)

    implementation(projects.core.billing)
    implementation(projects.core.data)
    implementation(projects.core.inappUpdate)
    implementation(projects.core.notification)
    implementation(projects.core.ui)
    implementation(projects.feature.addRecord)
    implementation(projects.feature.auth)
    implementation(projects.feature.colorTonePicker)
    implementation(projects.feature.editCategory)
    implementation(projects.feature.overview)
    implementation(projects.feature.records)
    implementation(projects.feature.insider)
    implementation(projects.feature.settings)
    implementation(projects.feature.unlockPremium)
    implementation(projects.feature.welcome)
}