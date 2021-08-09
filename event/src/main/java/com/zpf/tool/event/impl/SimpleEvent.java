package com.zpf.tool.event.impl;

import com.zpf.tool.event.api.IEvent;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Created by ZPF on 2021/6/7.
 */
public class SimpleEvent implements IEvent {
    private final int eventCode;
    private final LinkedList<String> eventReceivers = new LinkedList<>();
    private Object eventData;
    private String eventMsg;

    public SimpleEvent(int eventCode, Object eventData, String eventMsg) {
        this.eventCode = eventCode;
        this.eventData = eventData;
        this.eventMsg = eventMsg;
    }

    public SimpleEvent addReceiver(String name) {
        eventReceivers.add(name);
        return this;
    }

    public SimpleEvent setData(Object data) {
        eventData = data;
        return this;
    }

    @Override
    public int getEventCode() {
        return eventCode;
    }


    @Override
    public Object getEventData() {
        return eventData;
    }

    @Override
    public String getEventMessage() {
        return eventMsg;
    }

    @Override
    public List<String> receiverNames() {
        return eventReceivers;
    }
}
