package com.zpf.process.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.zpf.process.aidl.IHostHandler;
import com.zpf.process.aidl.IServiceHandler;
import com.zpf.process.aidl.OnBindService;
import com.zpf.process.manager.ServiceManagerImpl;

/**
 * @author Created by ZPF on 2021/3/25.
 */
public abstract class ProcessService extends Service {
    private ServiceManagerImpl serviceManager;
    private final OnBindService.Stub bindStub = new OnBindService.Stub() {

        @Override
        public IServiceHandler onBind(IHostHandler handler) throws RemoteException {
            if (serviceManager == null) {
                serviceManager = ServiceManagerImpl.get(ProcessService.this);
            }
            if (serviceManager != null) {
                serviceManager.registerHost(handler);
            }
            return serviceManager;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return bindStub;
    }

    public abstract int getServiceId();

    @Override
    public void onCreate() {
        super.onCreate();
        if (serviceManager == null) {
            serviceManager = ServiceManagerImpl.get(this);
        }
        if (serviceManager != null) {
            serviceManager.onServiceCreate(this);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        if (serviceManager != null) {
            serviceManager.onServiceDestroy(this);
        }
        super.onDestroy();
    }

}
