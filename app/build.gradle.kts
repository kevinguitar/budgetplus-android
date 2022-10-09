@file:Suppress("UnstableApiUsage")

import kotlin.math.pow

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    with(libs.plugins) {
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
    val appId = rootProject.ext["application_id"] as String
    val appVersion = rootProject.ext["app_version"] as String
    val targetSdkVersion = rootProject.ext["android_sdk"] as Int
    val minSdkVersion = rootProject.ext["min_android_sdk"] as Int

    /**
     *  Major version * 10^6
     *  Minor version * 10^3
     *  Bugfix version * 10^0
     *
     *  "1.1.4"     to 1001004
     *  "3.100.1"   to 3100001
     *  "10.52.39"  to 10052039
     */
    val appVersionCode = appVersion
        .split(".")
        .reversed()
        .mapIndexed { index, num ->
            num.toInt() * 10.0.pow(index * 3).toInt()
        }
        .sum()

    namespace = appId
    compileSdk = targetSdkVersion

    defaultConfig {
        applicationId = appId
        minSdk = minSdkVersion
        targetSdk = targetSdkVersion
        versionName = appVersion
        versionCode = appVersionCode

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