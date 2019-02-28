全局数据传递
=========
模拟快递柜存取进行全局数据传递，数据包裹不会马上投递给接收人，需要接收人自己查找；
### 使用方法
1. 包装要中转的数据；<br>
数据包裹分为3个部分：收件人、发件人、实际数据；详见[Expressage](src/main/java/com/zpf/tool/dhl/Expressage.java)
2. 将数据放入储存柜；
````
//使用默认数据储存柜存放数据；
DHL.get().put(new ExpressageInterface());
//使用指定id的数据储存柜存放数据
DHL.get().put(id，new ExpressageInterface());
````
3. 实现接收逻辑；
>主要方法如下：<br>
checkSender:检查发件人；<br>
receiveOwnerless:是否接收没有收件人的数据；<br>
unpackRemnants:拆包并处理指定给自己数据；<br>
unpackNow:拆包并处理收件人的数据；

详见[ParcelReceiver](src/main/java/com/zpf/tool/dhl/ParcelReceiver.java)
4. 将储存柜中的数据储交给对应接收人；
````
//从默认数据储存柜中查找数据
DHL.get().send(new ParcelReceiverInterface());
//从指定id的数据储存柜中查找数据
DHL.get().send(id，new ParcelReceiverInterface());
````

### 依赖
无
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
>'com.zpf.android:tool-dhl:latest.integration'