plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.payment"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.payment"
        minSdk = 28
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.6.0")
    implementation("androidx.navigation:navigation-ui:2.6.0")
    implementation("com.google.android.gms:play-services-tasks:18.0.2")
    implementation("com.google.firebase:firebase-auth:22.1.1")
    implementation("com.google.firebase:firebase-database:20.2.2")
    implementation("com.google.firebase:firebase-storage:20.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    //otp_view
    implementation("io.github.chaosleung:pinview:1.4.4")
    //firebase
    implementation(platform("com.google.firebase:firebase-bom:32.2.2"))
    implementation ("com.google.firebase:firebase-firestore:23.0.1")
    //duplicate class
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
    //scaneer
    implementation ("androidx.core:core-ktx:1.6.0")
    implementation ("androidx.camera:camera-camera2:1.1.0")
    implementation ("androidx.camera:camera-lifecycle:1.1.0")
    implementation ("androidx.camera:camera-view:1.0.0-alpha30")
    implementation ("com.google.mlkit:barcode-scanning:17.0.0")
    //lottie
    implementation ("com.airbnb.android:lottie:6.1.0")
    //qr code
    implementation ("com.google.zxing:core:3.4.1")
    implementation ("com.journeyapps:zxing-android-embedded:4.2.0")
    //circale img
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    //img input
    implementation ("com.github.dhaval2404:imagepicker:2.1")
    //load img
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    //animation
    implementation ("androidx.core:core-splashscreen:1.0.0")
    
}