plugins {
    id("com.android.application")
    alias(libs.plugins.kotlin.android)
}

android {
    val appId = "com.zpf.aaa"
    val minSdkVersion: Int by rootProject.extra
    val compileSdkVersion: Int by rootProject.extra
    val targetSdkVersion: Int by rootProject.extra
    namespace = appId
    compileSdk = compileSdkVersion
    defaultConfig {
        applicationId = appId
        minSdk = minSdkVersion
        targetSdk = targetSdkVersion
        versionCode = 1
        versionName = "1.0.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    viewBinding {
        enable = true
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(fileTree("libs"))
    implementation(libs.appcompat)
    implementation(libs.recyclerview)
    implementation(libs.viewPager)
    implementation(libs.viewPager2)
    implementation(libs.gson)
    implementation(libs.constraintlayout)
    implementation(libs.flexbox)
    implementation(project(":views"))
    implementation(project(":file"))
    implementation(project(":toolkit"))
    implementation(project(":compatPermission"))
    implementation(project(":synth"))
    implementation(project(":synth"))

    implementation("com.google.android.exoplayer:exoplayer-core:2.18.0")
    implementation("com.google.android.exoplayer:exoplayer-dash:2.18.0")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.18.0")
}

