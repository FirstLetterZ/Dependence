package com.zpf.tool.event.api;

/**
 * @author Created by ZPF on 2021/6/7.
 */
public interface IEventReceiver {
    String name();

    void onReceive(IEvent event, IEventRecord record);
}