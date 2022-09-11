buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.13")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.1")
    }
}

plugins {
    id("com.android.application") version "7.3.0-rc01" apply false
    id("com.android.test") version "7.3.0-rc01" apply false
    id("com.google.dagger.hilt.android") version "2.43.2" apply false
    id("org.jetbrains.kotlin.android") version "1.7.10" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.10" apply false
    id("org.jetbrains.kotlin.jvm") version "1.7.10" apply false
}