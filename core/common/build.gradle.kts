plugins {
    alias(budgetplus.plugins.android.library)
    alias(budgetplus.plugins.hilt)
    alias(budgetplus.plugins.kotlin.serialization)
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.runtime)
    implementation(libs.coil)
    implementation(libs.navigation.runtime)
    testFixturesImplementation(libs.android.activity)
}