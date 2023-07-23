plugins {
    id("budgetplus.android.library")
    id("budgetplus.hilt.android")
    id("budgetplus.kotlin.serialization")
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.google.play.review)
}