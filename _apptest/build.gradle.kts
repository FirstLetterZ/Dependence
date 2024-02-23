plugins {
    id("com.android.application")
}

android {
    val appId = "com.example.apptest"
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
    implementation(project(":api"))
    implementation(project(":views"))
    implementation(libs.tool.central)
    implementation(libs.appcompat)
}

