plugins {
    alias(budgetplus.plugins.android.library)
    alias(budgetplus.plugins.hilt)
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.google.play.update)
}