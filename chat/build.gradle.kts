// Top-level build.gradle file where you can add configuration options common to all sub-projects/modules.

plugins {
    id("com.android.application") version "8.7.2" apply false
    id("com.android.library") version "8.7.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

// Task to clean the project build directory
tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)
}