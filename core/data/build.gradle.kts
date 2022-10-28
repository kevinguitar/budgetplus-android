@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("budgetplus.android.library")
    id("budgetplus.hilt.android")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(project(":core:common"))

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.firestore)

    implementation(libs.kotlin.serialization)
    implementation(libs.google.auth)

    testImplementation(libs.bundles.test)
}