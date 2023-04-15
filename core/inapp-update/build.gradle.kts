plugins {
    id("budgetplus.android.library")
    id("budgetplus.hilt.android")
}

dependencies {
    implementation(project(":core:data"))
    implementation(libs.google.play.update)
}