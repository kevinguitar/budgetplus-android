plugins {
    id("budgetplus.android.library")
    id("budgetplus.hilt.android")
    id("budgetplus.kotlin.serialization")
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.data)

    implementation(platform(libs.firebase.bom))
    api(libs.firebase.messaging)
}