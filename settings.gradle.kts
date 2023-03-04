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

rootProject.name = "budgetplus"
include(":app")
include(":benchmark")
include(":core:billing")
include(":core:common")
include(":core:data")
include(":core:inapp-review")
include(":core:notification")
include(":core:ui")
include(":feature:add-record")
include(":feature:auth")
include(":feature:edit-category")
include(":feature:overview")
include(":feature:unlock-premium")
include(":feature:welcome")