plugins {
    alias(budgetplus.plugins.android.library)
    alias(budgetplus.plugins.compose)
    alias(budgetplus.plugins.metro)
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.settingsApi)
    implementation(projects.core.ui)

    implementation(libs.coil.compose)
}