@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "${rootProject.ext["application_id"] as String}.core.common"
    compileSdk = rootProject.ext["android_sdk"] as Int
    defaultConfig {
        minSdk = rootProject.ext["min_android_sdk"] as Int
    }
}

dependencies {
    api(libs.android.core)
    api(libs.coroutines)
    api(libs.timber)
    api(libs.hilt.android)
}