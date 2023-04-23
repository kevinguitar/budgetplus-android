plugins {
    id("budgetplus.android.library")
    id("budgetplus.hilt.android")
    id("budgetplus.kotlin.serialization")
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)

    implementation(libs.google.ads)
    implementation(libs.google.auth)
}