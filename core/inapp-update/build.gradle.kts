plugins {
    id("budgetplus.android.library")
    id("budgetplus.hilt.android")
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.google.play.update)
}