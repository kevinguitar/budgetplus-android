package common

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

object Constants {
    val javaVersion = JavaVersion.VERSION_11
    val jvmTarget = JvmTarget.JVM_11
}