plugins {
    alias(budgetplus.plugins.android.library)
}

dependencies {
    testFixturesImplementation(libs.coroutines.test)
    testFixturesImplementation(libs.junit.compose)
}
