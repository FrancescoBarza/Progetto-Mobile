plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    //aggiunti
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.example.appranzo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.appranzo"
        minSdk = 26
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
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
    //aggiunti
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.koin.androidx.compose)
    implementation(libs.androidx.ui.text.google.fonts)
    // Osmdroid
    implementation ("org.osmdroid:osmdroid-android:6.1.10")

    // Accompanist Permissions
    implementation ("com.google.accompanist:accompanist-permissions:0.30.1")

    // Lifecycle–Compose (for LocalLifecycleOwner)
    implementation ("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")

    // Preference KTX (for PreferenceManager)
    implementation ("androidx.preference:preference-ktx:1.2.0")


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("io.ktor:ktor-client-core:2.3.8")
    implementation("io.ktor:ktor-client-okhttp:2.3.8")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.8")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.8")
    implementation("androidx.datastore:datastore-preferences:1.1.3")
    implementation("io.coil-kt:coil-compose:2.3.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
}