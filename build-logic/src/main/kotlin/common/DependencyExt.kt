package common

import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.getByType

// Hack to support version catalog https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
inline val Project.libs get() =
    extensions.getByType<org.gradle.accessors.dm.LibrariesForLibs>()

fun DependencyHandlerScope.implementation(module: Any) {
    add("implementation", module)
}

fun DependencyHandlerScope.ksp(module: Any) {
    add("ksp", module)
}

fun DependencyHandlerScope.compileOnly(module: Any) {
    add("compileOnly", module)
}

fun DependencyHandlerScope.coreLibraryDesugaring(module: Any) {
    add("coreLibraryDesugaring", module)
}