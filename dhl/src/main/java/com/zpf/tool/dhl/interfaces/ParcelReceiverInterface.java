package com.zpf.tool.dhl.interfaces;

/**
 * 数据收件人
 * Created by ZPF on 2018/11/8.
 */
public interface ParcelReceiverInterface extends ParcelDisposeInterface, ParcelInspectorInterface {

    //接收人
    String getReceiver();
}
