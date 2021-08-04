package com.zpf.process.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.zpf.process.aidl.IServiceHandler;
import com.zpf.process.aidl.OnBindService;
import com.zpf.process.manager.HostManagerImpl;

/**
 * @author Created by ZPF on 2021/4/8.
 */
public class ServiceRecord {
    private final Class<?> serviceClass;
    public final int serviceId;
    private IServiceHandler serviceHandler;
    private String clientId;
    private Bundle params;
    public long startTime;
    HostManagerImpl hostManager;
    Bundle config;
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(final ComponentName componentName, final IBinder binder) {
            try {
                OnBindService bindService = OnBindService.Stub.asInterface(binder);
                serviceHandler = bindService.onBind(hostManager);
                if (clientId != null && clientId.length() > 0) {
                    serviceHandler.startToWork(clientId, params);
                }
                startTime = System.currentTimeMillis();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(final ComponentName componentName) {
            serviceHandler = null;
            clientId = null;
        }

    };

    public ServiceRecord(Class<?> serviceClass) {
        this.serviceClass = serviceClass;
        ServiceId id = this.serviceClass.getAnnotation(ServiceId.class);
        if (id == null) {
            serviceId = 0;
        } else {
            serviceId = id.value();
        }
    }

    public int search(String clientId) {
        if (serviceHandler == null) {
            this.clientId = null;
            this.params = null;
            return 3;
        }
        String currentClient = null;
        try {
            currentClient = serviceHandler.getCurrentClient();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (currentClient == null) {
            return 1;
        }
        if (currentClient.equals(clientId)) {
            return 0;
        }
        return 2;
    }

    public void startToWork(Context context, String clientId, Bundle params) throws RemoteException {
        this.clientId = clientId;
        if (config != null) {
            if (params == null) {
                this.params = config;
            } else {
                this.params = new Bundle();
                this.params.putAll(config);
                this.params.putAll(params);
            }
        } else {
            this.params = params;
        }
        if (serviceHandler != null) {
            if (clientId != null) {
                serviceHandler.startToWork(clientId, params);
                startTime = System.currentTimeMillis();
            }
        } else {
            Intent intent = new Intent(context, serviceClass);
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
    }

    public void startService(Context context) {
        if (serviceHandler == null) {
            Intent intent = new Intent(context, serviceClass);
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
    }

    public IServiceHandler getServiceHandler() {
        return serviceHandler;
    }

    public boolean updateServiceInfo(int serviceId, IServiceHandler serviceHandler) {
        if (this.serviceId != serviceId) {
            return false;
        }
        this.serviceHandler = serviceHandler;
        return true;
    }
}
