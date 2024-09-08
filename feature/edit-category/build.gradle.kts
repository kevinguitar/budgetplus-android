plugins {
    alias(budgetplus.plugins.android.library)
    alias(budgetplus.plugins.compose)
    alias(budgetplus.plugins.hilt)
    alias(budgetplus.plugins.kotlin.serialization)
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.ui)

    implementation(libs.reorderable)

    testImplementation(testFixtures(projects.core.data))
    testImplementation(testFixtures(projects.core.ui))
}