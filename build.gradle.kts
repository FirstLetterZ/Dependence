// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.library) apply false
}
buildscript {
    extra["compileSdkVersion"] = 34
    extra["targetSdkVersion"] = 33
    extra["minSdkVersion"] = 21
}
allprojects {
    repositories {
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://s01.oss.sonatype.org/content/groups/public")
        maven("https://maven.aliyun.com/repository/public/")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/central")
        maven("https://maven.aliyun.com/repository/jcenter")
        google()
        mavenCentral()
    }
    configurations.all {
        resolutionStrategy {
            val kotlinVersion = libs.versions.kotlin.asProvider().get()
//            val kotlinVersion ="1.9.21"
            force(libs.annotations)
            force(libs.appcompat)
            force(libs.activity)
            force(libs.fragment)
            force(libs.viewPager)
            force(libs.viewPager2)
            force(libs.ktx.core)
            force(libs.kotlin.coroutines)
            force("androidx.arch.core:core-runtime:2.2.0")
            force("org.jetbrains:annotations:23.0.0")
            force("org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}")
//            force("org.jetbrains.kotlin:kotlin-stdlib-common:${kotlinVersion}")
//            force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${kotlinVersion}")
//            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
//            force("org.jetbrains.kotlin:kotlin-coroutines-android:1.6.4")
//            force("org.jetbrains.kotlin:kotlin-coroutines-core-jvm:1.6.4")
        }
    }
}

