plugins {
    id("budgetplus.android.library")
    id("budgetplus.compose")
    id("budgetplus.hilt.android")
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.ui)
    // For displaying the color tone content preview
    implementation(projects.feature.categoryPills)

    implementation(libs.lottie.compose)
    implementation(libs.orchestra.colorpicker)
}