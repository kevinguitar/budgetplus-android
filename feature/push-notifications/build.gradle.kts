plugins {
    alias(budgetplus.plugins.android.library)
    alias(budgetplus.plugins.compose)
    alias(budgetplus.plugins.metro)
}

dependencies {
    implementation(libs.google.translate) {
        // Exclude artifacts that caused duplicated classes :/
        exclude("com.google.api.grpc", "proto-google-common-protos")
        exclude("com.google.protobuf", "protobuf-java")
    }
    // Without this it crashes at grpc usages internally :/
    implementation(platform("io.grpc:grpc-bom:1.76.0"))

    implementation(projects.core.data)
    implementation(projects.core.ui)
}