@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
}

android {
    val appId: String by rootProject.extra
    val minAndroidSdk: Int by rootProject.extra
    val androidSdk: Int by rootProject.extra

    namespace = "$appId.core.data"
    compileSdk = androidSdk
    defaultConfig {
        minSdk = minAndroidSdk
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