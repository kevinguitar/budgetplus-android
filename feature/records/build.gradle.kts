plugins {
    id("budgetplus.android.library")
    id("budgetplus.compose.library")
    id("budgetplus.hilt.android")
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.ui)
    implementation(projects.feature.recordCard)
}