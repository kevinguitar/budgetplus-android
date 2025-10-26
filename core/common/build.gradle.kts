plugins {
    alias(budgetplus.plugins.android.library)
    alias(budgetplus.plugins.hilt)
    alias(budgetplus.plugins.kotlin.serialization)
}

dependencies {
    api(libs.navigation3.runtime)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.runtime)
    implementation(libs.coil)
    testFixturesImplementation(libs.android.activity)
}