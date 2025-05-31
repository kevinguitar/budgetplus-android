plugins {
    alias(budgetplus.plugins.android.library)
    alias(budgetplus.plugins.hilt)
    alias(budgetplus.plugins.kotlin.serialization)
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)

    testFixturesImplementation(projects.core.common)
}