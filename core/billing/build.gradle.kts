plugins {
    id("budgetplus.android.library")
    id("budgetplus.hilt.android")
}

dependencies {
    implementation(project(":core:data"))

    implementation(libs.compose.activity)
    implementation(libs.google.billing)
}