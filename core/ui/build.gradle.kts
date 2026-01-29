plugins {
    alias(budgetplus.plugins.kotlin.multiplatform)
    alias(budgetplus.plugins.compose.multiplatform)
    alias(budgetplus.plugins.metro)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        optIn.add("androidx.compose.material3.ExperimentalMaterial3Api")
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.theme)
            implementation(libs.android.viewmodel.compose)
            implementation(project.libs.compose.material3)
            implementation(project.libs.compose.material3.windowsizeclass)
        }
        androidMain.dependencies {
            implementation(libs.lottie.compose)
        }
    }
}