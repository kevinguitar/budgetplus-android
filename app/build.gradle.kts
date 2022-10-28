@file:Suppress("UnstableApiUsage")

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("budgetplus.android.app")
    id("budgetplus.hilt.android")
    id("budgetplus.compose.app")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    implementation(libs.navigation.compose)
    implementation(libs.google.ads)
    implementation(libs.accompanist.flowlayout)
    implementation(libs.lottie.compose)
    implementation(libs.kotlin.serialization)

    implementation(libs.profile.installer)
    implementation(libs.google.billing)
    implementation(libs.exp4j)
    implementation(libs.coil.compose)

    implementation(project(":core:billing"))
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:ui"))
    implementation(project(":feature:add-record"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:edit-category"))
    implementation(project(":feature:overview"))
    implementation(project(":feature:records"))
    implementation(project(":feature:unlock-premium"))
    implementation(project(":feature:welcome"))
}