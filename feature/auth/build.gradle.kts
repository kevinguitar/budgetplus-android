plugins {
    id("budgetplus.android.library")
    id("budgetplus.compose.library")
    id("budgetplus.hilt.android")
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:ui"))

    implementation(libs.install.referrer)
    implementation(libs.facebook.auth)
    implementation(libs.firebase.auth)
    implementation(libs.google.auth)
}