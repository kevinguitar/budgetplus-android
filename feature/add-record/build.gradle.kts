plugins {
    alias(budgetplus.plugins.android.library)
    alias(budgetplus.plugins.compose)
    alias(budgetplus.plugins.hilt)
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.inappReview)
    implementation(projects.core.ui)
    implementation(projects.feature.categoryPills)
    implementation(projects.feature.speakToRecord)

    implementation(libs.lottie.compose)
    implementation(libs.exp4j)

    testImplementation(testFixtures(projects.core.data))
    testImplementation(testFixtures(projects.core.ui))
    testImplementation(testFixtures(projects.core.inappReview))
}