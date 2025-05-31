plugins {
    alias(budgetplus.plugins.insider.app)
    alias(budgetplus.plugins.hilt)
    alias(budgetplus.plugins.compose)
    alias(budgetplus.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.android.activity)
    implementation(libs.navigation.compose)

    implementation(projects.core.data)
    implementation(projects.core.impl)
    implementation(projects.core.ui)

    implementation(projects.feature.auth)
    implementation(projects.feature.pushNotifications)
    implementation(projects.feature.insider)
}