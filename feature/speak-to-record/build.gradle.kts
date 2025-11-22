plugins {
    alias(budgetplus.plugins.android.library)
    alias(budgetplus.plugins.compose)
    alias(budgetplus.plugins.metro)
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.data)

    implementation(libs.lottie.compose)
}