@file:Suppress("UnstableApiUsage")

import kotlin.math.pow

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("budgetplus.android.app")
    id("budgetplus.hilt.android")
    id("budgetplus.compose.app")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    val appId: String by project
    val appVersion: String by project
    val minAndroidSdk: String by project
    val androidSdk: String by project

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

    defaultConfig {
        applicationId = appId
        minSdk = minAndroidSdk.toInt()
        targetSdk = androidSdk.toInt()
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
            matchingFallbacks.add("release")
            isDebuggable = false
        }
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
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
    implementation(libs.bundles.navigation)
    implementation(libs.bundles.google.services)
    testImplementation(libs.bundles.test)

    implementation(libs.accompanist.flowlayout)
    implementation(libs.reorderable)
    implementation(libs.lottie.compose)
    implementation(libs.kotlin.serialization)

    implementation(libs.profile.installer)
    implementation(libs.google.billing)
    implementation(libs.install.referrer)
    implementation(libs.facebook.auth)
    implementation(libs.exp4j)
    implementation(libs.coil.compose)

    implementation(project(":core:billing"))
    implementation(project(":core:common"))
    implementation(project(":core:data"))
//    implementation(project(":core:ui"))
//    implementation(project(":feature:add-record"))
//    implementation(project(":feature:auth"))
//    implementation(project(":feature:edit-category"))
//    implementation(project(":feature:overview"))
//    implementation(project(":feature:records"))
//    implementation(project(":feature:unlock-premium"))
//    implementation(project(":feature:welcome"))
}