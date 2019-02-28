package com.zpf.tool.dhl.interfaces;

/**
 * 数据包裹检查
 * Created by ZPF on 2018/11/9.
 */
public interface ParcelInspectorInterface {
    //检查寄件人
    boolean checkSender(String sender);

    //接收无主的包裹
    boolean receiveOwnerless();
}
