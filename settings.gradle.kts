pluginManagement {
    repositories {
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://s01.oss.sonatype.org/content/groups/public")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/central")
        maven("https://maven.aliyun.com/repository/jcenter")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://jitpack.io")
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
include(
    ":aaa",
    ":_apptest",
    ":global",
    ":api",
    ":dataparser",
    ":process",
    ":permission",
    ":appstack",
    ":file",
//        ":fingerprint",
//    ":fragmentManager",
    ":wheelpicker",
    ":version",
//    ":compatFragmentManager",
    ":compatPermission",
    ":toolkit",
    ":views"
)