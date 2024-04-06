plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.cbocka.soundzen"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cbocka.soundzen"
        minSdk = 26
        targetSdk = 33
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.preference:preference:1.2.1")
    implementation("androidx.preference:preference-ktx:1.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Lottie Animations
    implementation("com.airbnb.android:lottie:6.2.0")

    //DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    //LiveData
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")

    //Youtube Player
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")

    //HTTP Requests
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.squareup.okhttp3:okhttp:4.9.2")

    //Music Player
    implementation("com.google.android.exoplayer:exoplayer-core:2.16.1")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.16.1")

    implementation("androidx.media:media:1.7.0")
}