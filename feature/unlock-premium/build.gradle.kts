plugins {
    id("budgetplus.android.library")
    id("budgetplus.compose.library")
    id("budgetplus.hilt.android")
}

dependencies {
    implementation(project(":core:billing"))
    implementation(project(":core:ui"))

    implementation(libs.lottie.compose)
}