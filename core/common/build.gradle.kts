@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    val appId: String by rootProject.extra
    val minAndroidSdk: Int by rootProject.extra
    val androidSdk: Int by rootProject.extra

    namespace = "$appId.core.common"
    compileSdk = androidSdk
    defaultConfig {
        minSdk = minAndroidSdk
    }
}

dependencies {
    api(libs.android.core)
    api(libs.coroutines)
    api(libs.timber)
    api(libs.hilt.android)
}