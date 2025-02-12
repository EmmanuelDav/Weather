import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    id ("kotlin-kapt")
}

android {
    namespace = "com.cyberiyke.weatherApp"
    compileSdk = 34



    defaultConfig {
        applicationId = "com.cyberiyke.weatherApp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    val localPropertiesFile = rootProject.file("local.properties")
    val localProperties = Properties()

    if (localPropertiesFile.exists()) {
        localProperties.load(localPropertiesFile.inputStream())
    }

    val apiKey = localProperties["API_KEY"]?.toString() ?: ""

    if (apiKey.isEmpty()) {
        throw GradleException("""
        API_KEY is missing in local.properties. 
        Please add your API key to the local.properties file as follows:

        API_KEY=your_api_key_here
        
        If you don’t have an API key, visit https://newsapi.org/ to generate one. """)
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("release") {
            buildConfigField("String", "API_KEY", "\"$apiKey\"")
        }
        getByName("debug") {
            buildConfigField("String", "API_KEY", "\"$apiKey\"")
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    //Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.paging.common.android)
    kapt(libs.hilt.android.compiler)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // LiveData
    implementation(libs.androidx.lifecycle.livedata.ktx)

    //Retrofit
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")


    //Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)


    // Timber logging
    implementation (libs.timber)

    //paging
    implementation (libs.androidx.paging.runtime)

    implementation (libs.androidx.room.paging)



    // Firebase Crashlytics
    implementation (libs.firebase.crashlytics)
    implementation (libs.firebase.analytics)

    // glide
    implementation (libs.github.glide)
    kapt (libs.compiler)

    //facebook shimmer
    implementation (libs.shimmer)


    //lifecycle
    implementation (libs.androidx.lifecycle.runtime.ktx) // Use the latest version


    // coil
    implementation (libs.coil)
    kapt(libs.androidx.room.compiler)

    //Coroutines
    implementation (libs.kotlinx.coroutines.core)
    implementation (libs.kotlinx.coroutines.android)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    //testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}