package com.zpf.process.model;

import android.os.Bundle;
import android.util.Log;

import com.zpf.process.ProcessAdmin;
import com.zpf.process.api.IEventCallback;
import com.zpf.process.api.IEventHandler;
import com.zpf.process.api.IPredicate;

import java.util.HashMap;

/**
 * @author Created by ZPF on 2021/4/12.
 */
public class EventHandlerMap {
    private final HashMap<String, String> indexMap = new HashMap<>();
    private final HashMap<String, IEventHandler> dataMap = new HashMap<>();

    public void add(IEventHandler handler) {
        if (handler == null) {
            return;
        }
        String dataKey = getDataKey(handler);
        dataMap.put(dataKey, handler);
        EventIndex eventIndex = handler.getClass().getAnnotation(EventIndex.class);
        if (eventIndex == null) {
            return;
        }
        String[] events = eventIndex.events();
        if (events.length == 0) {
            return;
        }
        String preString;
        for (String ev : events) {
            preString = indexMap.put(ev, dataKey);
            if (preString != null && ProcessAdmin.DEBUGGABLE) {
                Log.w("EventHandler", "replace event handler:eventName=" + ev + ";newHandlerClass=" + handler.getClass().getName());
            }
        }
    }

    public void setHandler(String event, IEventHandler handler) {
        if (event == null) {
            return;
        }
        indexMap.remove(event);
        if (handler == null) {
            return;
        }
        String dataKey = getDataKey(handler);
        dataMap.remove(dataKey);
    }

    public boolean remove(IEventHandler handler) {
        String dataKey = getDataKey(handler);
        return dataMap.remove(dataKey) != null;
    }

    public void clear() {
        dataMap.clear();
        indexMap.clear();
    }

    public IEventHandler find(IPredicate<IEventHandler> predicate) {
        if (predicate == null) {
            return null;
        }
        IEventHandler result = null;
        for (IEventHandler handler : dataMap.values()) {
            if (predicate.test(handler)) {
                result = handler;
                break;
            }
        }
        return result;
    }

    public boolean dispatch(String event, Bundle params, IEventCallback eventCallback) {
        if (event == null) {
            return false;
        }
        boolean handled = false;
        String dataKey = indexMap.size() == 0 ? null : indexMap.get(event);
        if (dataKey != null && dataKey.length() > 0) {
            IEventHandler dataHandler = dataMap.get(dataKey);
            if (dataHandler != null) {
                handled = dataHandler.handleEvent(event, params, eventCallback);
            } else {
                indexMap.remove(event);
            }
        }
        if (!handled) {
            for (IEventHandler handler : dataMap.values()) {
                EventIndex eventIndex = handler.getClass().getAnnotation(EventIndex.class);
                if (eventIndex != null && eventIndex.events().length > 0) {
                    continue;
                }
                if (handled = handler.handleEvent(event, params, eventCallback)) {
                    break;
                }
            }
        }
        return handled;
    }

    private String getDataKey(Object obj) {
        if (obj == null) {
            return "";
        }
        return String.valueOf(obj.hashCode());
    }
}