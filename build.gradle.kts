// Top-level build file
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0" apply false
}

// Redirect build directory to D: drive to save space on C:
allprojects {
    buildDir = file("D:/AndroidBuilds/${rootProject.name}/${project.name}")
}
