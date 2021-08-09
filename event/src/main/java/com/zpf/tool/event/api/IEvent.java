package com.zpf.tool.event.api;

import java.util.List;

/**
 * 事件协议
 * Created by ZPF on 2019/7/18.
 */
public interface IEvent {
    int getEventCode();

    Object getEventData();

    String getEventMessage();

    List<String> receiverNames();
}
