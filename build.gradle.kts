plugins {
    // AGP
    alias(libs.plugins.android.application) apply false

    // Kotlin
    alias(libs.plugins.jetbrains.kotlin.android) apply false

    // KSP
    alias(libs.plugins.ksp) apply false

    // Hilt
    alias(libs.plugins.hiltCompiler) apply false

    // Navigation Safe Args
    alias(libs.plugins.navsafeargs) apply false
}

buildscript {
    dependencies {
        classpath(libs.okHttpClient)
        classpath (libs.gradle)
    }
}