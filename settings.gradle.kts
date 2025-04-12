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
    versionCatalogs {
        register("budgetplus") {
            from(files("gradle/plugins.versions.toml"))
        }
    }
}

rootProject.name = "budgetplus"
include(":app")
include(":benchmark")
include(":core:billing")
include(":core:common")
include(":core:data")
include(":core:impl")
include(":core:inapp-review")
include(":core:inapp-update")
include(":core:notification")
include(":core:settings-api")
include(":core:theme")
include(":core:ui")
include(":core:unit-test")
include(":feature:add-record")
include(":feature:auth")
include(":feature:category-pills")
include(":feature:color-tone-picker")
include(":feature:currency-picker")
include(":feature:edit-category")
include(":feature:insider")
include(":feature:overview")
include(":feature:push-notifications")
include(":feature:record-card")
include(":feature:records")
include(":feature:settings")
include(":feature:speak-to-record")
include(":feature:unlock-premium")
include(":feature:welcome")