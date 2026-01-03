plugins {
    alias(budgetplus.plugins.android.library)
    alias(budgetplus.plugins.metro)
}

dependencies {
    implementation(projects.core.data)

    implementation(libs.google.billing)
}