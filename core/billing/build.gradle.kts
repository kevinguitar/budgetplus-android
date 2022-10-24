@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "${rootProject.ext["application_id"] as String}.core.billing"
    compileSdk = rootProject.ext["android_sdk"] as Int
    defaultConfig {
        minSdk = rootProject.ext["min_android_sdk"] as Int
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.compose.activity)
    implementation(libs.google.billing)
}