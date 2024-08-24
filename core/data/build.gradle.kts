plugins {
    alias(budgetplus.plugins.android.library)
    alias(budgetplus.plugins.hilt)
    alias(budgetplus.plugins.kotlin.serialization)
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.config)

    implementation(libs.google.auth)
    implementation(libs.google.ads)
    runtimeOnly(libs.google.ads.mediation.meta)

    implementation(libs.compose.icons)
}