plugins {
    id("budgetplus.android.app")
    id("budgetplus.hilt.android")
    id("budgetplus.compose.app")
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)

    implementation(libs.navigation.compose)
    implementation(libs.google.ads)
    implementation(libs.profile.installer)

    implementation(project(":core:billing"))
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:notification"))
    implementation(project(":core:ui"))
    implementation(project(":feature:add-record"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:edit-category"))
    implementation(project(":feature:overview"))
    implementation(project(":feature:records"))
    implementation(project(":feature:unlock-premium"))
    implementation(project(":feature:welcome"))
}