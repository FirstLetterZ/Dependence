基础接口
=========
* [ActivityController](./src/main/java/com/zpf/api/ActivityController.java)
---Activity跳转、关闭，获取上下文，获取Activity实例，获取Intent；
* [CallBackInterface](./src/main/java/com/zpf/api/CallBackInterface.java)
---绑定管理器，达到条件自动取消；
* [CallBackManagerInterface](./src/main/java/com/zpf/api/CallBackManagerInterface.java)
---CallBackInterface管理器，触发onDestroy方法时应取消全部CallBackInterface；
* [ContainerProcessorInterface](./src/main/java/com/zpf/api/ContainerProcessorInterface.java)
---视图处理层接口；
* [CreatorInterface](./src/main/java/com/zpf/api/CreatorInterface.java)
---泛型对象创建器；
* [IconText](./src/main/java/com/zpf/api/IconText.java)
---可使用成文字、图片、IconFont的视图；
* [KVPInterface](./src/main/java/com/zpf/api/KVPInterface.java)
---键值对通用接口；
* [LifecycleInterface](./src/main/java/com/zpf/api/LifecycleInterface.java)
---生命周期的对应回调；
* [LifecycleListenerController](./src/main/java/com/zpf/api/LifecycleListenerController.java)
---获取生命周期状态，以及管理常用的有生命周期的管理器；
* [LoggerInterface](./src/main/java/com/zpf/api/LoggerInterface.java)
---日志输出接口；
* [OnDestroyListener](./src/main/java/com/zpf/api/OnDestroyListener.java)
---对应生命周期触发onDestroy方法时的回调；
* [OnItemClickListener](./src/main/java/com/zpf/api/OnItemClickListener.java)
---列表item点击回调；
* [OnProgressChangedListener](./src/main/java/com/zpf/api/OnProgressChangedListener.java)
---进度变化监听；
* [OnResultListener](./src/main/java/com/zpf/api/OnProgressChangedListener.java)
---通用回调接口；
* [PackedLayoutInterface](./src/main/java/com/zpf/api/PackedLayoutInterface.java)
---多视图包装，管理内部视图显示、遮挡、切换；
* [PermissionCheckerInterface](./src/main/java/com/zpf/api/PermissionCheckerInterface.java)
---权限检查与申请；
* [ResultCallBackListener](./src/main/java/com/zpf/api/ResultCallBackListener.java)
---监听onActivityResult、onRequestPermissionsResult、onNewIntent以及可见性变化；
* [RootLayoutInterface](./src/main/java/com/zpf/api/RootLayoutInterface.java)
---带标题栏、状态栏的基本视图；
* [RouteInterface](./src/main/java/com/zpf/api/RouteInterface.java)
---跳转路由；
* [SafeWindowInterface](./src/main/java/com/zpf/api/SafeWindowInterface.java)
---绑定生命周期的弹窗；
* [SafeWindowController](./src/main/java/com/zpf/api/SafeWindowController.java)
---弹窗管理器；
* [StorageManagerInterface](./src/main/java/com/zpf/api/StorageManagerInterface.java)
---存储管理接口；
* [StorageQueueInterface](./src/main/java/com/zpf/api/StorageQueueInterface.java)
---存储队列接口；
* [TitleBarInterface](./src/main/java/com/zpf/api/TitleBarInterface.java)
---标题栏；
* [UrlInterceptor](./src/main/java/com/zpf/api/UrlInterceptor.java)
---url拦截；
* [VariableParameterInterface](./src/main/java/com/zpf/api/VariableParameterInterface.java)
---动态获取对应值；
* [ViewContainerInterface](./src/main/java/com/zpf/api/ViewContainerInterface.java)
---视图容器，目前为Activity或Fragment；

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
>'com.zpf.android:api-kit:latest.integration'