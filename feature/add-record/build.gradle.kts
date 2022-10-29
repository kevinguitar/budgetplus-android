plugins {
    id("budgetplus.android.library")
    id("budgetplus.compose.library")
    id("budgetplus.hilt.android")
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:ui"))

    implementation(libs.accompanist.flowlayout)
    implementation(libs.coil.compose)
    implementation(libs.lottie.compose)
    implementation(libs.exp4j)
}