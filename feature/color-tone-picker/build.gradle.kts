plugins {
    alias(budgetplus.plugins.android.library)
    alias(budgetplus.plugins.compose)
    alias(budgetplus.plugins.hilt)
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.ui)
    // For displaying the color tone content preview
    implementation(projects.feature.categoryPills)

    implementation(libs.lottie.compose)
    implementation(libs.orchestra.colorpicker)
}