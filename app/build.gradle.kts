plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "bdisfer1410.controldepresencia"
    compileSdk = 35

    defaultConfig {
        applicationId = "bdisfer1410.controldepresencia"
        minSdk = 29
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Android
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Api
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.gson)
    // Geolocalización
    implementation(libs.play.services.location)
}