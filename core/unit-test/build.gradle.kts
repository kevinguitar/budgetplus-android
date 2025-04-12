plugins {
    alias(budgetplus.plugins.android.library)
}

dependencies {
    testFixturesImplementation(libs.coroutines.test)
    testFixturesImplementation(platform(libs.compose.bom))
    testFixturesImplementation(libs.junit.compose)
}
