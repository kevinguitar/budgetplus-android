plugins {
    id("budgetplus.android.library")
    id("budgetplus.compose.library")
    id("budgetplus.hilt.android")
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:inapp-review"))
    implementation(project(":core:ui"))
    implementation(project(":feature:category-pills"))

    implementation(libs.lottie.compose)
    implementation(libs.exp4j)
}