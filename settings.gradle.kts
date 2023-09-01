pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "budgetplus"
include(":app")
include(":benchmark")
include(":core:billing")
include(":core:common")
include(":core:data")
include(":core:inapp-review")
include(":core:inapp-update")
include(":core:notification")
include(":core:theme")
include(":core:ui")
include(":feature:add-record")
include(":feature:auth")
include(":feature:category-pills")
include(":feature:color-tone-picker")
include(":feature:edit-category")
include(":feature:insider")
include(":feature:overview")
include(":feature:record-card")
include(":feature:records")
include(":feature:settings")
include(":feature:unlock-premium")
include(":feature:welcome")