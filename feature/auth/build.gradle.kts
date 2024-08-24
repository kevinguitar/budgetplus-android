plugins {
    alias(budgetplus.plugins.android.library)
    alias(budgetplus.plugins.compose)
    alias(budgetplus.plugins.hilt)
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.ui)

    implementation(libs.android.activity)
    implementation(libs.install.referrer)
    implementation(libs.firebase.auth)
    implementation(libs.google.auth)
}