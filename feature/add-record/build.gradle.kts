plugins {
    id("budgetplus.android.library")
    id("budgetplus.compose")
    id("budgetplus.hilt.android")
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.inappReview)
    implementation(projects.core.ui)
    implementation(projects.feature.categoryPills)

    implementation(libs.lottie.compose)
    implementation(libs.exp4j)
}