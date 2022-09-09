package buildSrc.src.main.kotlin

object Versions {

    // Version of Android Gradle Plugin
    // https://maven.google.com/web/index.html?q=gradle#com.android.tools.build:gradle
    const val ANDROID_GRADLE_PLUGIN_VERSION = "7.2.2"

    // Kotlin
    // Full release notest - https://github.com/JetBrains/kotlin/releases/tag/vKOTLIN_VERSION
    // release - https://kotlinlang.org/docs/releases.html#release-details
    // eap - https://kotlinlang.org/docs/eap.html#build-details
    // Before updating version of Kotlin always check version supported by Compose
    // https://developer.android.com/jetpack/compose/interop/adding#anchor
    const val KOTLIN_VERSION = "1.7.10"

    // Compose UI
    // https://developer.android.com/jetpack/androidx/releases/compose-ui
    const val COMPOSE_VERSION = "1.2.1"

    // Compose compiler
    // https://developer.android.com/jetpack/androidx/releases/compose-compiler
    const val COMPOSE_COMPILER_VERSION = "1.3.0"

    // Dagger Hilt
    // https://dagger.dev/hilt/gradle-setup
    const val DAGGER_HILT_VERSION = "2.43.2"

    // Firebase BOM
    // https://firebase.google.com/support/release-notes/android
    const val FIREBASE_BOM_VERSION = "30.4.0"

    // Google Play billing
    // https://developer.android.com/google/play/billing/release-notes
    const val BILLING_VERSION = "5.0.0"

}