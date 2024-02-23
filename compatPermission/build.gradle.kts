plugins {
    id("com.android.library")
}
android {
    namespace= "com.zpf.tool.compat.permission"
    val minSdkVersion: Int by rootProject.extra
    val compileSdkVersion: Int by rootProject.extra
    compileSdk = compileSdkVersion
    defaultConfig {
        minSdk = minSdkVersion
    }
}
dependencies {
    compileOnly("androidx.fragment:fragment:1.4.1")
    api("io.github.firstletterz:tool-permission:0.3.0")
}