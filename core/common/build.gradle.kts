plugins {
    alias(budgetplus.plugins.kotlin.multiplatform)
    alias(budgetplus.plugins.metro)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.navigation3.runtime)
            implementation(libs.coil)
            implementation(libs.kotlin.datetime)
        }

        androidMain.dependencies {
            implementation(libs.android.activity)
        }
    }
}
