package buildSrc.src.main.kotlin

object Libraries {

    // Android core
    const val androidCore = "androidx.core:core-ktx:1.9.0"
    const val androidLifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:2.5.1"

    // Jetpack Compose
    const val composeActivity = "androidx.activity:activity-compose:1.6.0"
    const val composeMaterial = "androidx.compose.material:material:${Versions.composeVersion}"
    const val composeUi = "androidx.compose.ui:ui:${Versions.composeVersion}"
    const val composeUiPreview = "androidx.compose.ui:ui-tooling-preview:${Versions.composeVersion}"
    const val composeUiTooling = "androidx.compose.ui:ui-tooling:${Versions.composeVersion}"
    const val composeUiTest = "androidx.compose.ui:ui-test-manifest:${Versions.composeVersion}"

    // Compose Compiler
    const val composeCompiler = "androidx.compose.compiler:compiler:${Versions.composeCompilerVersion}"

    // Compose UI utils
    // https://google.github.io/accompanist/flowlayout/
    const val accompanistFlowLayout = "com.google.accompanist:accompanist-flowlayout:0.25.1"

    // Lottie
    // https://github.com/airbnb/lottie/blob/master/android-compose.md
    const val lottieCompose = "com.airbnb.android:lottie-compose:5.2.0"

    // Kotlinx serialization
    // https://github.com/Kotlin/kotlinx.serialization
    const val kotlinSerialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0"

    // Navigation
    // https://developer.android.com/jetpack/androidx/releases/navigation
    const val navigationCompose = "androidx.navigation:navigation-compose:2.5.2"
    const val navigationHilt = "androidx.hilt:hilt-navigation-compose:1.0.0"

    // Dagger Hilt
    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.daggerHiltVersion}"
    const val hiltCompiler = "com.google.dagger:hilt-compiler:${Versions.daggerHiltVersion}"

    // Baseline profile
    const val profileInstaller = "androidx.profileinstaller:profileinstaller:${Versions.profileInstallerVersion}"

    // Firebase
    const val firebaseBom = "com.google.firebase:firebase-bom:${Versions.firebaseBomVersion}"
    const val firebaseAuth = "com.google.firebase:firebase-auth-ktx"
    const val firebaseFirestore = "com.google.firebase:firebase-firestore-ktx"
    const val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics-ktx"
    const val firebaseAnalytics = "com.google.firebase:firebase-analytics-ktx"

    // Google Play billing
    const val googlePlayBilling = "com.android.billingclient:billing-ktx:${Versions.billingVersion}"

    // Install Referrer
    const val installReferrer = "com.android.installreferrer:installreferrer:2.2"

    // 3rd party auth
    const val googleAuth = "com.google.android.gms:play-services-auth:20.3.0"
    const val facebookAuth = "com.facebook.android:facebook-login:13.0.0"

    // AdMob
    const val googleAds = "com.google.android.gms:play-services-ads:21.2.0"

    // Utils
    const val timber = "com.jakewharton.timber:timber:5.0.1"
    const val exp4j = "net.objecthunter:exp4j:0.4.8"
    const val coilCompose = "io.coil-kt:coil-compose:2.1.0"
    const val desugar = "com.android.tools:desugar_jdk_libs:1.2.2"

    // Testing
    const val junit = "junit:junit:4.13.2"
    const val truth = "com.google.truth:truth:1.1.3"
    const val junitTest = "androidx.test.ext:junit:1.1.3"
    const val espresso = "androidx.test.espresso:espresso-core:3.4.0"
    const val junitCompose = "androidx.compose.ui:ui-test-junit4:${Versions.composeVersion}"
}