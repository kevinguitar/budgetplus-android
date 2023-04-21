plugins {
    id("budgetplus.android.library")
    id("budgetplus.compose.library")
    id("budgetplus.hilt.android")
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:ui"))

    implementation(libs.accompanist.flowlayout)
    implementation(libs.kotlin.csv)
    implementation(libs.lottie.compose)

    testImplementation(libs.bundles.test)
}