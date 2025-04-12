plugins {
    alias(budgetplus.plugins.android.library)
    alias(budgetplus.plugins.hilt)
    alias(budgetplus.plugins.kotlin.serialization)
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.inappUpdate)
    implementation(projects.core.inappReview)
    implementation(libs.google.play.update)
    implementation(libs.google.play.review)
    testImplementation(testFixtures(projects.core.data))
}