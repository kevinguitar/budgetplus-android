plugins {
    id("budgetplus.android.library")
    id("budgetplus.compose")
    id("budgetplus.hilt.android")
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.ui)

    implementation(libs.android.activity)
    implementation(libs.install.referrer)
    implementation(libs.firebase.auth)
    implementation(libs.google.auth)
}