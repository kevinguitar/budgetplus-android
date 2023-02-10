plugins {
    id("budgetplus.android.library")
    id("budgetplus.hilt.android")
}

dependencies {
    implementation(project(":core:data"))

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
}