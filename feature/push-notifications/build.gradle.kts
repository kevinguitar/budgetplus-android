plugins {
    id("budgetplus.android.library")
    id("budgetplus.compose.library")
    id("budgetplus.hilt.android")
}

dependencies {
    implementation(libs.google.translate) {
        // Excluding them for dependencies resolution collision
        exclude(group = "com.google.api.grpc")
        exclude(group = "com.google.protobuf")
    }

    implementation(projects.core.data)
    implementation(projects.core.ui)
}