plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
//    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
//    id("com.google.dagger.hilt.android")
}


android {
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/io.netty.versions.properties"
        }
    }
    namespace = "com.example.topmovers"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.topmovers"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
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
    implementation(libs.firebase.appdistribution.gradle)
    implementation(libs.androidx.media3.common.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.coil.compose)
////    implementation(libs.coil3.coil.compose)
////    implementation(libs.coil.network.okhttp)
//    implementation("io.coil-kt.coil3:coil-compose:3.1.0")
//    implementation("io.coil-kt.coil3:coil-network-okhttp:3.1.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")

    val nav_version = "2.8.9"
    implementation("androidx.navigation:navigation-compose:$nav_version")
//    //Firebase
//    implementation(platform("com.google.firebase:firebase-bom:34.1.0"))
//    implementation("com.google.firebase:firebase-auth")
//    implementation("com.google.firebase:firebase-firestore")
//    //Hilt
//    implementation("com.google.dagger:hilt-android:2.56.1")
//    ksp("com.google.dagger:hilt-android-compiler:2.56.1")
//    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
//
    //Room
    //val room_version = "2.7.1"
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
//    implementation("androidx.room:room-ktx:$room_version")
//
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")

    implementation(libs.converter.gson)
    implementation(libs.converter.gson)
    implementation(libs.converter.gson.v290)
    implementation("androidx.compose.material:material-icons-extended-android:1.6.7")
    implementation("com.patrykandpatrick.vico:compose:1.14.0")
    implementation("com.patrykandpatrick.vico:core:1.14.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")



}