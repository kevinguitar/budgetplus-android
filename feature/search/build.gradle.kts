plugins {
    alias(budgetplus.plugins.android.library)
    alias(budgetplus.plugins.compose)
    alias(budgetplus.plugins.hilt)
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)

    implementation(projects.core.data)
    implementation(projects.core.ui)
    implementation(projects.feature.categoryPills)
    implementation(projects.feature.recordCard)

    testImplementation(testFixtures(projects.core.data))
}