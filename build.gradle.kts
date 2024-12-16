// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()  // Repository untuk Google
        mavenCentral()  // Repository untuk Maven Central
    }
    dependencies {
        // Menambahkan plugin Google Services
        classpath ("com.android.tools.build:gradle:8.7.3")
        classpath ("com.google.gms:google-services:4.4.2")
    }
}





