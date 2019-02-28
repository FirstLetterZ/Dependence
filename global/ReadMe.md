全局配置工具
=========
* [GlobalConfigImpl](./src/main/java/com/zpf/tool/config/GlobalConfigImpl.java)
---单例；
* [GlobalConfigInterface](./src/main/java/com/zpf/tool/config/GlobalConfigInterface.java)
---接口；

### 使用方法
在程序启动开始后完成初始化；
### 依赖
无
### 引用
在项目对应build.gradle文件内的allprojects-repositories下添加工具包仓库地址：
````
allprojects {
    repositories {
        maven { url 'https://dl.bintray.com/letterz/AndroidSupportMaven' }
    }
}
````
在Module内添加对应引用：
>'com.zpf.android:tool-config:latest.integration'