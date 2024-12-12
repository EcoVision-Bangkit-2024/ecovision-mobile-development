// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}

buildscript {
    repositories {
        google()  // Repositori untuk Android Tools
        mavenCentral()  // Repositori untuk dependensi umum
        maven { url = uri("https://jitpack.io") }  // JitPack untuk library pihak ketiga
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.2")  // Plugin Android Gradle
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0")  // Plugin Kotlin
    }
}

allprojects {
    // Jangan menambahkan repositori lagi di sini jika sudah ada di settings.gradle.kts
}