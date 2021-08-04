package com.zpf.process.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.zpf.process.ProcessAdmin;
import com.zpf.process.aidl.ICallback;
import com.zpf.process.aidl.IHostHandler;
import com.zpf.process.aidl.IServiceHandler;
import com.zpf.process.aidl.OnBindHost;
import com.zpf.process.api.IEventCallback;
import com.zpf.process.api.IEventHandler;
import com.zpf.process.api.IManager;
import com.zpf.process.api.IPredicate;
import com.zpf.process.api.IServiceListener;
import com.zpf.process.api.IServiceManager;
import com.zpf.process.api.IServiceStaff;
import com.zpf.process.model.EventHandlerMap;
import com.zpf.process.service.HostService;
import com.zpf.process.service.ProcessService;
import com.zpf.process.util.ProcessUtil;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Created by ZPF on 2021/4/8.
 */
public class ServiceManagerImpl extends IServiceHandler.Stub implements IServiceManager {
    private final Context appContext;
    private IHostHandler hostHandler;
    private String clientId;
    private int serviceId;
    private IServiceStaff serviceStaff;
    private final EventHandlerMap handlerMap = new EventHandlerMap();
    private IServiceListener serviceListener;
    private final ConcurrentHashMap<String, IEventCallback> callbackMap = new ConcurrentHashMap<>();
    private final ICallback.Stub serviceCallback = new ICallback.Stub() {

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
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(final ComponentName componentName, final IBinder binder) {
            try {
                OnBindHost bindHost = OnBindHost.Stub.asInterface(binder);
                IHostHandler handler = bindHost.onBind(serviceId, ServiceManagerImpl.this);
                if (handler != null) {
                    hostHandler = handler;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(final ComponentName componentName) {
            hostHandler = null;
        }
    };

    public void bindHost() {
        Intent intent = new Intent(appContext, HostService.class);
        appContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void registerHost(IHostHandler manager) {
        hostHandler = manager;
    }

    @Override
    public void invoke(final String event, final Bundle params, final ICallback callback) throws RemoteException {
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

    @Override
    public void startToWork(String client, Bundle params) throws RemoteException {
        clientId = client;
        if (serviceStaff != null) {
            if (params != null) {
                params.setClassLoader(getClass().getClassLoader());
            }
            serviceStaff.startToWork(appContext, serviceId, client, params);
        }
    }

    @Override
    public void stopAllWork() throws RemoteException {
        if (serviceStaff != null) {
            serviceStaff.stopAllWork(serviceId);
        }
        clearCallback();
    }

    @Override
    public String getCurrentClient() throws RemoteException {
        return clientId;
    }

    private ServiceManagerImpl(Context context) {
        appContext = context.getApplicationContext();
    }

    private static volatile ServiceManagerImpl manager;

    public static ServiceManagerImpl get(Context context) {
        if (!ProcessUtil.isServiceProcess(context)) {
            return null;
        }
        if (manager == null) {
            synchronized (ServiceManagerImpl.class) {
                if (manager == null) {
                    manager = new ServiceManagerImpl(context);
                }
            }
        }
        return manager;
    }

    public void clearCallback(){
        callbackMap.clear();
    }

    public void onServiceCreate(ProcessService service) {
        if (service == null || serviceId != 0) {
            return;
        }
        final int sid = service.getServiceId();
        serviceId = sid;
        if (serviceListener != null) {
            serviceListener.onServiceCreate(service, sid);
        }
    }

    public void onServiceDestroy(ProcessService service) {
        clientId = null;
        final int sid;
        if (service == null || (sid = service.getServiceId()) != serviceId) {
            return;
        }
        if (serviceListener != null) {
            serviceListener.onServiceDestroy(service, sid);
        }
        serviceId = 0;
    }

    @Override
    public IServiceManager addEventHandler(IEventHandler handler) {
        handlerMap.add(handler);
        return this;
    }

    @Override
    public IServiceManager removeEventHandler(IEventHandler handler) {
        handlerMap.remove(handler);
        return this;
    }

    @Override
    public void dispatchEvent(String event, Bundle params, IEventCallback eventCallback) {
        if (!handlerMap.dispatch(event, params, eventCallback)) {
            sendToHost(event, params, eventCallback);
        }
    }

    @Override
    public void sendToHost(String event, Bundle params, IEventCallback eventCallback) {
        if (hostHandler != null) {
            if (eventCallback != null) {
                callbackMap.put(event, eventCallback);
            } else {
                callbackMap.remove(event);
            }
            try {
                hostHandler.handleMessage(clientId, event, params, serviceCallback);
                return;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (eventCallback != null) {
            callbackMap.remove(event);
            eventCallback.callback(false, null);
        }
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
    public IServiceManager setServiceListener(IServiceListener listener) {
        serviceListener = listener;
        return this;
    }

    @Override
    public IServiceManager setServiceStaff(IServiceStaff staff) {
        serviceStaff = staff;
        return this;
    }

    @Override
    public int getServiceId() {
        return serviceId;
    }

}