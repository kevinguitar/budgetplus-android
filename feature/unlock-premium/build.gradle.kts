plugins {
    alias(budgetplus.plugins.android.library)
    alias(budgetplus.plugins.compose)
    alias(budgetplus.plugins.metro)
}

dependencies {
    implementation(projects.core.billing)
    implementation(projects.core.data)
    implementation(projects.core.ui)

    implementation(libs.lottie.compose)
}