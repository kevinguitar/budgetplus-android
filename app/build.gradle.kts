@file:Suppress("UnstableApiUsage")

import buildSrc.src.main.kotlin.AppConfig
import buildSrc.src.main.kotlin.Versions

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = AppConfig.APPLICATION_ID
    compileSdk = AppConfig.ANDROID_SDK_VERSION

    defaultConfig {
        applicationId = AppConfig.APPLICATION_ID
        minSdk = AppConfig.MIN_ANDROID_SDK_VERSION
        targetSdk = AppConfig.ANDROID_SDK_VERSION
        versionName = AppConfig.APP_VERSION
        versionCode = AppConfig.appVersionCode

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = rootProject.file("BudgetKey")
            storePassword = "budget+"
            keyAlias = "key0"
            keyPassword = "budget+"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        create("benchmark") {
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }

    buildFeatures.compose = true
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.COMPOSE_COMPILER_VERSION
    }
    packagingOptions {
        resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    }
    bundle {
        storeArchive.enable = false
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
        )
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {

    // Android core
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")

    // Jetpack Compose
    implementation("androidx.activity:activity-compose:1.6.0")
    implementation("androidx.compose.material:material:${Versions.COMPOSE_VERSION}")
    implementation("androidx.compose.ui:ui:${Versions.COMPOSE_VERSION}")
    implementation("androidx.compose.ui:ui-tooling-preview:${Versions.COMPOSE_VERSION}")
    debugImplementation("androidx.compose.ui:ui-tooling:${Versions.COMPOSE_VERSION}")
    debugImplementation("androidx.compose.ui:ui-test-manifest:${Versions.COMPOSE_VERSION}")
    // Compose Compiler
    implementation("androidx.compose.compiler:compiler:${Versions.COMPOSE_COMPILER_VERSION}")
    // Compose UI utils
    // https://google.github.io/accompanist/flowlayout/
    implementation("com.google.accompanist:accompanist-flowlayout:0.25.1")

    // Lottie
    // https://github.com/airbnb/lottie/blob/master/android-compose.md
    implementation("com.airbnb.android:lottie-compose:5.2.0")

    // Kotlinx serialization
    // https://github.com/Kotlin/kotlinx.serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")

    // Navigation
    // https://developer.android.com/jetpack/androidx/releases/navigation
    implementation("androidx.navigation:navigation-compose:2.5.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:${Versions.DAGGER_HILT_VERSION}")
    kapt("com.google.dagger:hilt-compiler:${Versions.DAGGER_HILT_VERSION}")

    // Baseline profile
    implementation("androidx.profileinstaller:profileinstaller:${Versions.PROFILE_INSTALLER_VERSION}")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:${Versions.FIREBASE_BOM_VERSION}"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Google Play billing
    implementation("com.android.billingclient:billing-ktx:${Versions.BILLING_VERSION}")

    // Install Referrer
    implementation("com.android.installreferrer:installreferrer:2.2")

    // 3rd party auth
    implementation("com.google.android.gms:play-services-auth:20.3.0")
    implementation("com.facebook.android:facebook-login:13.0.0")

    // AdMob
    implementation("com.google.android.gms:play-services-ads:21.2.0")

    // Utils
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("net.objecthunter:exp4j:0.4.8")
    implementation("io.coil-kt:coil-compose:2.1.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.2.2")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.truth:truth:1.1.3")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${Versions.COMPOSE_VERSION}")

}