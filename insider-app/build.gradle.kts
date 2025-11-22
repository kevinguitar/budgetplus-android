plugins {
    alias(budgetplus.plugins.insider.app)
    alias(budgetplus.plugins.metro)
    alias(budgetplus.plugins.compose)
    alias(budgetplus.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.navigation3.ui)
    implementation(libs.navigation3.viewmodel)

    implementation(projects.core.data)
    implementation(projects.core.impl)
    implementation(projects.core.ui)

    implementation(projects.feature.auth)
    implementation(projects.feature.pushNotifications)
    implementation(projects.feature.insider)
}