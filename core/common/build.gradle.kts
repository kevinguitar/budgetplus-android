plugins {
    id("budgetplus.android.library")
    id("budgetplus.compose")
    id("budgetplus.hilt.android")
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.coil)
}