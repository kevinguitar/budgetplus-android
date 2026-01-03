plugins {
    alias(budgetplus.plugins.android.library)
    alias(budgetplus.plugins.metro)
    alias(budgetplus.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.firebase.auth)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.config)

    implementation(projects.core.data)
    implementation(projects.core.inappUpdate)
    implementation(projects.core.inappReview)
    implementation(projects.core.ui)
    implementation(libs.google.play.update)
    implementation(libs.google.play.review)
    testImplementation(testFixtures(projects.core.data))
}