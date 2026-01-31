plugins {
    alias(budgetplus.plugins.kotlin.multiplatform)
    alias(budgetplus.plugins.metro)
    alias(budgetplus.plugins.compose.multiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.navigation3.runtime)
            implementation(libs.coil)
            api(libs.compose.resources)
        }

        androidMain.dependencies {
            implementation(libs.android.activity)
        }
    }
}

compose.resources {
    publicResClass = true
}