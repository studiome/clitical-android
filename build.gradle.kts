// Top-level build file where you can add configuration options common to all sub-projects/modules.

// AGP 9 bundles KGP 2.2.10 for built-in Kotlin. Declaring a newer KGP here makes
// AGP use it. Keep this in sync with `kotlin` in gradle/libs.versions.toml
// (the Compose compiler plugin must match the KGP version).
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.21")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
