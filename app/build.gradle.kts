@file:Suppress("UnstableApiUsage")

import buildSrc.src.main.kotlin.AppConfig

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    with (libs.plugins) {
        alias(android.application)
        alias(kotlin.android)
        alias(kotlin.kapt)
        alias(kotlin.serialization)
        alias(hilt.android)
        alias(google.services)
        alias(firebase.crashlytics)
    }
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
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packagingOptions {
        resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    }
    bundle {
        storeArchive.enable = false
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    with(libs) {
        implementation(platform(firebase.bom))
        implementation(bundles.firebase)
        implementation(bundles.compose)
        implementation(bundles.navigation)
        implementation(bundles.google.services)
        implementation(bundles.test)

        implementation(android.core)
        implementation(android.lifecycle)

        implementation(accompanist.flowlayout)
        implementation(lottie.compose)
        implementation(kotlin.serialization)

        implementation(hilt.android)
        kapt(hilt.compiler)

        implementation(profile.installer)
        implementation(google.billing)
        implementation(install.referrer)
        implementation(facebook.auth)
        implementation(timber)
        implementation(exp4j)
        implementation(coil.compose)
        coreLibraryDesugaring(desugar)
    }
}