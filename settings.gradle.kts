@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "BudgetPlus"
include(":app")
include(":app-benchmark")
include(":core:billing")
include(":core:common")
include(":core:data")
include(":core:ui")
//include(":feature:add-record")
include(":feature:auth")
//include(":feature:edit-category")
//include(":feature:overview")
//include(":feature:records")
//include(":feature:unlock-premium")
//include(":feature:welcome")