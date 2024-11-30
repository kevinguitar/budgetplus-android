plugins {
    alias(budgetplus.plugins.android.library)
    alias(budgetplus.plugins.compose)
    alias(budgetplus.plugins.hilt)
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.data)

    implementation(libs.lottie.compose)
}