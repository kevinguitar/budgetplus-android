plugins {
    id("budgetplus.android.library")
    id("budgetplus.compose")
    id("budgetplus.hilt.android")
}

dependencies {
    implementation(projects.core.billing)
    implementation(projects.core.data)
    implementation(projects.core.ui)

    implementation(libs.lottie.compose)
}