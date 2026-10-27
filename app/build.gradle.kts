plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services") version "4.4.1"
    id("kotlin-kapt")
}

android {
    namespace = "com.example.activitytrackerai"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.activitytrackerai"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation ("androidx.fragment:fragment-ktx:1.6.2")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation ("androidx.viewpager2:viewpager2:1.1.0-beta02")
    implementation ("com.google.android.material:material:1.11.0")

    // Για HTTP requests
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
// Για JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")
// Για JWT (Google)
    implementation("com.auth0:java-jwt:4.4.0")


    //Firebase
    implementation (platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation ("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")

    //Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    //Accompanist Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    //Room Database
    implementation ("androidx.room:room-runtime:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")

    //Google maps
    implementation ("com.google.maps.android:maps-compose:4.2.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
}