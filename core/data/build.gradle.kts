@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "${rootProject.ext["application_id"] as String}.core.data"
    compileSdk = rootProject.ext["android_sdk"] as Int
    defaultConfig {
        minSdk = rootProject.ext["min_android_sdk"] as Int
    }
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    implementation(libs.kotlin.serialization)
    implementation(libs.google.auth)
}