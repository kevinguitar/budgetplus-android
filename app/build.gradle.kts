plugins {
    alias(budgetplus.plugins.android.app)
    alias(budgetplus.plugins.hilt)
    alias(budgetplus.plugins.compose)
    alias(budgetplus.plugins.kotlin.serialization)
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    implementation(libs.android.activity)
    implementation(libs.navigation.compose)
    implementation(libs.profile.installer)
    implementation(libs.google.ads)
    implementation(libs.lottie.compose)

    implementation(projects.core.billing)
    implementation(projects.core.data)
    implementation(projects.core.impl)
    implementation(projects.core.inappUpdate)
    implementation(projects.core.notification)
    implementation(projects.core.ui)

    implementation(projects.feature.addRecord)
    implementation(projects.feature.auth)
    implementation(projects.feature.colorTonePicker)
    implementation(projects.feature.currencyPicker)
    implementation(projects.feature.editCategory)
    implementation(projects.feature.overview)
    implementation(projects.feature.pushNotifications)
    implementation(projects.feature.records)
    implementation(projects.feature.insider)
    implementation(projects.feature.search)
    implementation(projects.feature.settings)
    implementation(projects.feature.unlockPremium)
    implementation(projects.feature.welcome)
}