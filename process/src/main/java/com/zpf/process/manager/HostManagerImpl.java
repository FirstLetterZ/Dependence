package com.zpf.process.manager;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;

import com.zpf.process.ProcessAdmin;
import com.zpf.process.aidl.ICallback;
import com.zpf.process.aidl.IHostHandler;
import com.zpf.process.aidl.IServiceHandler;
import com.zpf.process.api.IEventCallback;
import com.zpf.process.api.IEventHandler;
import com.zpf.process.api.IHostManager;
import com.zpf.process.api.IManager;
import com.zpf.process.api.IPredicate;
import com.zpf.process.model.EventHandlerMap;
import com.zpf.process.util.ProcessUtil;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Created by ZPF on 2021/4/8.
 */
public class HostManagerImpl extends IHostHandler.Stub implements IHostManager {
    private final EventHandlerMap handlerMap = new EventHandlerMap();
    private final ConcurrentHashMap<String, IEventCallback> callbackMap = new ConcurrentHashMap<>();
    private final ICallback.Stub hostCallback = new ICallback.Stub() {

        @Override
        public void callback(String event, Bundle params) throws RemoteException {
            String eventName;
            boolean handled;
            if (event != null && event.startsWith(ProcessAdmin.NO_HANDLER_PREFIX)) {
                eventName = event.substring(ProcessAdmin.NO_HANDLER_PREFIX.length());
                handled = false;
            } else {
                eventName = event;
                handled = true;
            }
            IEventCallback eventCallback = callbackMap.remove(eventName);
            if (eventCallback != null) {
                eventCallback.callback(handled, params);
            }
        }
    };

    private HostManagerImpl() {
    }

    private static volatile HostManagerImpl manager;

    public static HostManagerImpl get(Context context) {
        if (!ProcessUtil.isHostProcess(context)) {
            return null;
        }
        if (manager == null) {
            synchronized (HostManagerImpl.class) {
                if (manager == null) {
                    manager = new HostManagerImpl();
                }
            }
        }
        return manager;
    }

    @Override
    public IHostManager addEventHandler(IEventHandler handler) {
        handlerMap.add(handler);
        return this;
    }

    @Override
    public IHostManager removeEventHandler(IEventHandler handler) {
        handlerMap.remove(handler);
        return this;
    }

    @Override
    public IManager setEventHandler(String event, IEventHandler handler) {
        handlerMap.setHandler(event, handler);
        return this;
    }

    @Override
    public IEventHandler findEventHandler(IPredicate<IEventHandler> predicate) {
        return handlerMap.find(predicate);
    }

    @Override
    public IManager clearEventHandler() {
        handlerMap.clear();
        return this;
    }

    @Override
    public void dispatchEvent(String event, Bundle params, IEventCallback eventCallback) {
        boolean handled = handlerMap.dispatch(event, params, eventCallback);
        if (handled || params == null) {
            return;
        }
        String clientId = params.getString("clientId");
        if (clientId == null || clientId.length() == 0) {
            return;
        }
        sendToService(clientId, event, params, eventCallback);
    }

    @Override
    public void sendToService(String clientId, String event, Bundle params, IEventCallback eventCallback) {
        IServiceHandler serviceHandler = ProcessAdmin.getServiceHandler(clientId);
        if (serviceHandler == null) {
            if (eventCallback != null) {
                eventCallback.callback(false, null);
            }
            return;
        }
        if (eventCallback != null) {
            callbackMap.put(event, eventCallback);
        } else {
            callbackMap.remove(event);
        }
        try {
            serviceHandler.invoke(event, params, hostCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
            if (eventCallback != null) {
                callbackMap.remove(event);
                eventCallback.callback(false, null);
            }
        }
    }

    @Override
    public void handleMessage(String clientId, final String event, final Bundle params, final ICallback callback) throws RemoteException {
        IEventCallback eventCallback = new IEventCallback() {
            @Override
            public void callback(boolean handled, Bundle bundle) {
                if (callback != null) {
                    try {
                        if (handled) {
                            callback.callback(event, bundle);
                        } else {
                            callback.callback(ProcessAdmin.NO_HANDLER_PREFIX + event, null);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        if (!handlerMap.dispatch(event, params, eventCallback)) {
            eventCallback.callback(false, null);
        }
    }

}