plugins {
    id("budgetplus.android.library")
    id("budgetplus.hilt.android")
}

dependencies {
    implementation(libs.coil)
    testImplementation(libs.bundles.test)
}