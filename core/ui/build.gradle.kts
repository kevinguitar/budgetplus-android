plugins {
    id("budgetplus.android.library")
    id("budgetplus.compose.library")
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.compose.material)
}