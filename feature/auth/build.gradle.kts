plugins {
    id("budgetplus.android.library")
    id("budgetplus.compose.library")
    id("budgetplus.hilt.android")
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.ui)

    implementation(libs.android.credentials)
    // For devices running Android 13 and below
    // See: https://developer.android.com/jetpack/androidx/releases/credentials
    implementation(libs.android.credentials.play.services)
    implementation(libs.install.referrer)
    implementation(libs.firebase.auth)
    implementation(libs.google.id)
}