基础工具
=========
* [frament文件夹](./src/main/java/com/zpf/tool/fragment)
---创建、查找、显示Fragment；
* [AppContext](./src/main/java/com/zpf/tool/AppContext.java)
---全局Application单例；
* [AsyncTaskInfo](./src/main/java/com/zpf/tool/AsyncTaskInfo.java)
---异步任务，配合AsyncTaskQueue使用；
* [AsyncTaskQueue](./src/main/java/com/zpf/tool/AsyncTaskQueue.java)
---异步任务队列单例，配合AsyncTaskInfo使用；
* [AutoSave](./src/main/java/com/zpf/tool/AutoSave.java)
---反射保存注解；
* [AutoSaveUtil](./src/main/java/com/zpf/tool/AutoSaveUtil.java)
---利用反射保存注解到达保存与重新赋值功能；
* [DataDefault](./src/main/java/com/zpf/tool/DataDefault.java)
---各类型数据的默认值；
* [FileUtil](./src/main/java/com/zpf/tool/FileUtil.java)
---获取存储路径、压缩文件、解压文件、写入文件、读取文件、读取Properties、获取Provider；
* [MainHandler](./src/main/java/com/zpf/tool/MainHandler.java)
---主线程Handler单例；
* [PublicUtil](./src/main/java/com/zpf/tool/PublicUtil.java)
---获取color文件定义的颜色、获取string文件定义的字符串、获取版本号、获取版本名称、获取设备编号；
* [SafeClickListener](./src/main/java/com/zpf/tool/SafeClickListener.java)
---需要校验点击间隔及自定义检查的条件的点击事件回调；
* [SplitTextWatcher](./src/main/java/com/zpf/tool/SplitTextWatcher.java)
---监听内容自动分隔字符串；
* [TimeCountUtil](./src/main/java/com/zpf/tool/TimeCountUtil.java)
---计时器工具；
* [TimeTaskUtil](./src/main/java/com/zpf/tool/TimeTaskUtil.java)
---定时执行任务；
* [ToastUtil](./src/main/java/com/zpf/tool/ToastUtil.java)
---自定义视图的Toast弹窗；
* [ViewUtil](./src/main/java/com/zpf/tool/ViewUtil.java)
---视图上下边界判断，键盘弹出收起；

### 依赖
com.android.support:support-v4<br>
com.android.support:recyclerview-v7
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
>'com.zpf.android:tool-kit:latest.integration'