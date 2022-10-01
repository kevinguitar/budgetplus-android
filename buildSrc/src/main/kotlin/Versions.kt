package buildSrc.src.main.kotlin

object Versions {

    // Version of Android Gradle Plugin
    // https://maven.google.com/web/index.html?q=gradle#com.android.tools.build:gradle
    const val agpVersion = "7.3.0"

    // Kotlin
    // Full release notes - https://github.com/JetBrains/kotlin/releases
    // release - https://kotlinlang.org/docs/releases.html#release-details
    // eap - https://kotlinlang.org/docs/eap.html#build-details
    // Before updating version of Kotlin always check version supported by Compose
    // https://developer.android.com/jetpack/compose/interop/adding#anchor
    const val kotlinVersion = "1.7.10"

    // Compose UI
    // https://developer.android.com/jetpack/androidx/releases/compose-ui
    const val composeVersion = "1.2.1"

    // Compose compiler
    // https://developer.android.com/jetpack/androidx/releases/compose-compiler
    const val composeCompilerVersion = "1.3.1"

    // Dagger Hilt
    // https://dagger.dev/hilt/gradle-setup
    const val daggerHiltVersion = "2.43.2"

    // Firebase BOM
    // https://firebase.google.com/support/release-notes/android
    const val firebaseBomVersion = "30.5.0"

    // Google Play billing
    // https://developer.android.com/google/play/billing/release-notes
    const val billingVersion = "5.0.0"

    // Profile Installer
    // https://developer.android.com/jetpack/androidx/releases/profileinstaller
    const val profileInstallerVersion = "1.2.0"

}