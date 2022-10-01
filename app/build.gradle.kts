@file:Suppress("UnstableApiUsage")

import buildSrc.src.main.kotlin.AppConfig
import buildSrc.src.main.kotlin.Libraries

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
        kotlinCompilerExtensionVersion = Libraries.composeCompilerVersion
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
    implementation(Libraries.androidCore)
    implementation(Libraries.androidLifecycle)

    // Jetpack Compose
    implementation(Libraries.composeActivity)
    implementation(Libraries.composeMaterial)
    implementation(Libraries.composeUi)
    implementation(Libraries.composeUiPreview)
    implementation(Libraries.composeUiTooling)
    implementation(Libraries.composeUiTest)
    // Compose Compiler
    implementation(Libraries.composeCompiler)
    // Compose UI utils
    implementation(Libraries.accompanistFlowLayout)

    // Lottie
    implementation(Libraries.lottieCompose)

    // Kotlinx serialization
    implementation(Libraries.kotlinSerialization)

    // Navigation
    // https://developer.android.com/jetpack/androidx/releases/navigation
    implementation(Libraries.navigationCompose)
    implementation(Libraries.navigationHilt)

    // Dagger Hilt
    implementation(Libraries.hiltAndroid)
    kapt(Libraries.hiltCompiler)

    // Baseline profile
    implementation(Libraries.profileInstaller)

    // Firebase
    implementation(platform(Libraries.firebaseBom))
    implementation(Libraries.firebaseAuth)
    implementation(Libraries.firebaseFirestore)
    implementation(Libraries.firebaseCrashlytics)
    implementation(Libraries.firebaseAnalytics)

    // Google Play billing
    implementation(Libraries.googlePlayBilling)

    // Install Referrer
    implementation(Libraries.installReferrer)

    // 3rd party auth
    implementation(Libraries.googleAuth)
    implementation(Libraries.facebookAuth)

    // AdMob
    implementation(Libraries.googleAds)

    // Utils
    implementation(Libraries.timber)
    implementation(Libraries.exp4j)
    implementation(Libraries.coilCompose)
    coreLibraryDesugaring(Libraries.desugar)

    // Testing
    testImplementation(Libraries.junit)
    testImplementation(Libraries.truth)
    androidTestImplementation(Libraries.junitTest)
    androidTestImplementation(Libraries.espresso)
    androidTestImplementation(Libraries.junitCompose)

}