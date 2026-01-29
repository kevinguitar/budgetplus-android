plugins {
    alias(budgetplus.plugins.kotlin.multiplatform)
}

dependencies {
    testFixturesImplementation(libs.coroutines.test)
    testFixturesImplementation(libs.junit.compose)
}