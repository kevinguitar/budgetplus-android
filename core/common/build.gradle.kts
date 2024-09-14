plugins {
    alias(budgetplus.plugins.android.library)
    alias(budgetplus.plugins.compose)
    alias(budgetplus.plugins.hilt)
    alias(budgetplus.plugins.kotlin.serialization)
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.coil)
    implementation(libs.navigation.runtime)
}