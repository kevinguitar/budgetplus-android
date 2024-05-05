plugins {
    id("budgetplus.android.library")
    id("budgetplus.compose")
    id("budgetplus.hilt.android")
    id("budgetplus.kotlin.serialization")
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.ui)

    implementation(libs.reorderable)
}