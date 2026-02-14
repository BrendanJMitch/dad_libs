import java.time.Year

plugins {
    alias(libs.plugins.android.application)
}

val buildYear = Year.now().value

android {
    namespace = "com.brendan.dadlibs"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.brendan.dadlibs"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.1.0"
        buildConfigField("String", "COPYRIGHT", "\"Copyright © $buildYear Brendan Mitchell\"")

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
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.room.runtime)
    implementation(libs.room.common.jvm)
    implementation(libs.room.runtime.android)
    implementation(libs.gson)
    implementation(libs.process.phoenix)
    implementation(libs.androidx.preference)
    implementation(libs.markwon.core)
    implementation(libs.markwon.ext.tables)
    implementation(libs.markwon.ext.strikethrough)

    annotationProcessor(libs.room.compiler)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.espresso.core.v351)
}