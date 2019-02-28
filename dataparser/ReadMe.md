json字符串解析基础依赖
=========
* [JsonParserInterface](./src/main/java/com/zpf/api/dataparser/JsonParserInterface.java)
---常用json解析接口；
* [StringParseResult](./src/main/java/com/zpf/api/dataparser/StringParseResult.java)
---字符串解析接口；
* [StringParseType](./src/main/java/com/zpf/api/dataparser/StringParseType.java)
---解析结果枚举；

### 依赖
com.android.support:support-annotations<br>
### 引用
在项目对应build.gradle文件内的allprojects-repositories下添加工具包仓库地址：
``````
allprojects {
    repositories {
        maven { url 'https://dl.bintray.com/letterz/AndroidSupportMaven' }
    }
}
``````
在Module内添加对应引用：
>'com.zpf.android:api-parse:latest.integration'