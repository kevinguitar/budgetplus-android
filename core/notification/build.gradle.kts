plugins {
    alias(budgetplus.plugins.android.library)
    alias(budgetplus.plugins.metro)
    alias(budgetplus.plugins.kotlin.serialization)
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.data)

    implementation(platform(libs.firebase.bom))
    api(libs.firebase.messaging)
}