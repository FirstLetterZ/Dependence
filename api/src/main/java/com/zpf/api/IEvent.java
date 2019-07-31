package com.zpf.api;

/**
 * 事件协议
 * Created by ZPF on 2019/7/18.
 */
public interface IEvent<T> {
    int getEventCode();

    String getEventName();

    T getEventData();

    String getEventMessage();

}
