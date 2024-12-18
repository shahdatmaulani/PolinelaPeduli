plugins {
    // Plugin untuk aplikasi Android
    id("com.android.application")

    // Plugin untuk Google services
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.polinelapeduli"
    compileSdk = 34

    lint {
        baseline = file("lint-baseline.xml")
    }

    defaultConfig {
        applicationId = "com.example.polinelapeduli"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth:23.0.0")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")


    // Firebase Analytics dan BOM untuk versi yang tepat
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-analytics")

    // Dependensi yang sudah ada
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(libs.activity)
    implementation(libs.firebase.database)
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.github.bumptech.glide:glide:4.15.0")
    implementation(libs.firebase.firestore)
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.0")
    implementation("com.google.firebase:firebase-firestore:25.1.1")
    implementation("com.android.volley:volley:1.2.1")


    // payment gateway
    implementation("com.midtrans:uikit:2.0.0-SANDBOX")
    implementation("com.midtrans:uikit:1.23.1")




    // Testing libraries
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
}

