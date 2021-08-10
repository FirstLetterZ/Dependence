Android工具库
=========
### 主要内容包括：

项目内的各项依赖库版本由[build](./build.gradle)文件统一管理

工具库发布
---------
#### 单项目发布
> ./gradlew -p XXX clean build publish --info
 
其中 XXX 为 module库名，详细如下表：

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
