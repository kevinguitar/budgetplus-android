plugins {
    alias(budgetplus.plugins.android.library)
    alias(budgetplus.plugins.metro)
    alias(budgetplus.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.firebase.firestore)

    testFixturesImplementation(projects.core.common)
}