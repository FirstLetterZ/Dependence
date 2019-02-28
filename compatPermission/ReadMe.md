权限检查与提示
=========
* [ActivityPermissionChecker](src/main/java/com/zpf/tool/compat/permission/ActivityPermissionChecker.java)
---Activity请求权限；
* [CompatFragmentPermissionChecker](src/main/java/com/zpf/tool/compat/permission/CompatFragmentPermissionChecker.java)
---android.support.v4.app.Fragment检查权限；
* [FragmentPermissionChecker](src/main/java/com/zpf/tool/compat/permission/FragmentPermissionChecker.java)
---android.app.Fragment检查权限；
* [OnLockPermissionRunnable](src/main/java/com/zpf/tool/compat/permission/OnLockPermissionRunnable.java)
---缺少权限时的回调；
* [PermissionChecker](src/main/java/com/zpf/tool/compat/permission/PermissionChecker.java)
---权限检查的基类，权限检查的结果处理，Toast权限检查，权限描述；
* [PermissionInfo](src/main/java/com/zpf/tool/compat/permission/PermissionInfo.java)
---权限信息；
* [PermissionManager](src/main/java/com/zpf/tool/compat/permission/PermissionManager.java)
---跳转到权限设置；

### 依赖
com.android.support:support-v4
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
>'com.zpf/tool.android:tool-permission:latest.integration'