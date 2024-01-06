package common

import org.gradle.kotlin.dsl.DependencyHandlerScope

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