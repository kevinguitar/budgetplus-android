plugins {
    alias(budgetplus.plugins.android.library)
    alias(budgetplus.plugins.compose)
    alias(budgetplus.plugins.hilt)
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.inappReview)
    implementation(projects.core.ui)
    implementation(projects.feature.categoryPills)

    implementation(libs.lottie.compose)
    implementation(libs.exp4j)
}