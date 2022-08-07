Android工具库
=========
### 主要内容包括：

项目内的各项依赖库版本由[build](./build.gradle)文件统一管理

工具库发布
---------
#### 单项目发布
> ./gradlew -p global clean build publish --info
./gradlew -p api clean build publish --info
./gradlew -p appstack clean build publish --info
./gradlew -p views clean build publish --info
./gradlew -p dataparser clean build publish --info
./gradlew -p process clean build publish --info
./gradlew -p permission clean build publish --info
./gradlew -p file clean build publish --info
./gradlew -p fragmentManager clean build publish --info
./gradlew -p wheelpicker clean build publish --info
./gradlew -p version clean build publish --info
./gradlew -p toolkit clean build publish --info
./gradlew -p compatFragmentManager clean build publish --info
./gradlew -p compatPermission clean build publish --info
./gradlew -p compatPermission clean build publish --info

引用
---------
如果需要使用快照版本，则需要在在项目对应build.gradle文件内添加仓库地址：
````
allprojects {
    repositories {
           maven { url 'https://s01.oss.sonatype.org/content/repositories/snapshots/'}
    }
}
````
