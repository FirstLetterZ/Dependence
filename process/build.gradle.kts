apply(from = "../publish.gradle")
plugins {
    id("com.android.library")
}
android {
    namespace = "com.zpf.process"
    val minSdkVersion: Int by rootProject.extra
    val compileSdkVersion: Int by rootProject.extra
    compileSdk = compileSdkVersion
    defaultConfig {
        minSdk = minSdkVersion
    }
}
dependencies {
    compileOnly(libs.annotations)
}