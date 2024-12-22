plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hiltCompiler)
    alias(libs.plugins.navsafeargs)
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.asahi"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.asahi"
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
            isShrinkResources = false
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
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
    }
}

dependencies {

    // Room
    implementation(libs.androidx.room.ktx)

    // SwipeRefreshLayout
    implementation(libs.androidx.swiperefreshlayout)

    // Hilt
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Core
    implementation(libs.core)
    implementation(libs.conscrypt.android)
    implementation(libs.bottomsheets)

    // Navigation
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Fragment & RecyclerView
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.recyclerview)

    // OkHttp
    implementation(libs.okHttpClient)
    implementation(libs.logging.interceptor)

    // CircleImageView
    implementation(libs.circleimageview)

    // Glide
    implementation(libs.glide)

    // ViewPager2
    implementation(libs.androidx.viewpager2)

    // Circle & dots (indicator)
    implementation(libs.circelIndicator)
    implementation(libs.dotsindicator)

    // Multidex
    implementation (libs.multidex)

    // Paging
    implementation(libs.androidx.paging.common.ktx)
    implementation(libs.androidx.paging.runtime.ktx)

    // Google Play Services
    implementation (libs.play.services.maps)

    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // Material Design
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // JUnit Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}