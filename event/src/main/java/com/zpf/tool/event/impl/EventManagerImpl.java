package com.zpf.tool.event.impl;

import android.util.LruCache;

import com.zpf.tool.event.api.IEvent;
import com.zpf.tool.event.api.IEventManager;
import com.zpf.tool.event.api.IEventReceiver;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Created by ZPF on 2021/6/7.
 */
public class EventManagerImpl implements IEventManager {
    private static class Instance {
        private static final EventManagerImpl mInstance = new EventManagerImpl();
    }

    public static EventManagerImpl get() {
        return Instance.mInstance;
    }

    private final ConcurrentHashMap<String, IEventReceiver> receiverMap = new ConcurrentHashMap<>();
    private final LruCache<String, EventRecord> waitHandlerMap = new LruCache<>(16);

    private EventManagerImpl() {
    }

    @Override
    public boolean register(IEventReceiver receiver) {
        if (receiver == null) {
            return false;
        }
        String name = receiver.name();
        if (name == null || name.length() == 0) {
            return false;
        }
        receiverMap.put(name, receiver);
        if (waitHandlerMap.size() > 0) {
            EventRecord record = waitHandlerMap.remove(name);
            if (record != null) {
                if (record.isInterrupt()) {
                    for (String rn : record.event.receiverNames()) {
                        waitHandlerMap.remove(rn);
                    }
                } else {
                    receiver.onReceive(record.event, record);
                    record.addReader(name);
                }
            }
        }
        return true;
    }

    @Override
    public boolean unregister(IEventReceiver receiver) {
        if (receiver == null) {
            return false;
        }
        String name = receiver.name();
        if (name == null || name.length() == 0) {
            return false;
        }
        return receiverMap.remove(name) != null;
    }

    @Override
    public void post(IEvent event) {
        if (event == null) {
            return;
        }
        handleEvent(event, false);
    }

    @Override
    public void postInfallible(IEvent event) {
        if (event == null) {
            return;
        }
        handleEvent(event, true);
    }


    private void handleEvent(IEvent event, boolean infallible) {
        List<String> receiverList = event.receiverNames();
        EventRecord eventRecord = new EventRecord(event);
        if (receiverList == null || receiverList.size() == 0) {
            for (Map.Entry<String, IEventReceiver> entry : receiverMap.entrySet()) {
                entry.getValue().onReceive(event, eventRecord);
                eventRecord.addReader(entry.getKey());
                if (eventRecord.isInterrupt()) {
                    break;
                }
            }
        } else {
            LinkedList<String> unHandList = null;
            for (String name : receiverList) {
                IEventReceiver receiver = receiverMap.get(name);
                if (receiver == null) {
                    if (infallible) {
                        if (unHandList == null) {
                            unHandList = new LinkedList<>();
                        }
                        unHandList.add(name);
                    }
                } else {
                    receiver.onReceive(event, eventRecord);
                    eventRecord.addReader(receiver.name());
                    if (eventRecord.isInterrupt()) {
                        break;
                    }
                }
            }
            if (!eventRecord.isInterrupt() && unHandList != null) {
                for (String it : unHandList) {
                    waitHandlerMap.put(it, eventRecord);
                }
            }
        }
    }
}
