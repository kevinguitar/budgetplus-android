@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
}

android {
    val appId: String by rootProject.extra
    val minAndroidSdk: Int by rootProject.extra
    val androidSdk: Int by rootProject.extra

    namespace = "$appId.core.billing"
    compileSdk = androidSdk
    defaultConfig {
        minSdk = minAndroidSdk
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