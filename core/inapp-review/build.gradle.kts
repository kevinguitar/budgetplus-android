plugins {
    id("budgetplus.android.library")
    id("budgetplus.hilt.android")
    id("budgetplus.kotlin.serialization")
}

dependencies {
    implementation(project(":core:data"))
    implementation(libs.google.play.review)

    testImplementation(libs.bundles.test)
}