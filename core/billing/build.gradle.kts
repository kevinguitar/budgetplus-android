plugins {
    alias(budgetplus.plugins.android.library)
    alias(budgetplus.plugins.metro)
}

dependencies {
    implementation(projects.core.data)

    implementation(libs.compose.activity)
    implementation(libs.google.billing)
}