package com.zpf.process.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.zpf.process.ProcessAdmin;
import com.zpf.process.aidl.IHostHandler;
import com.zpf.process.aidl.IServiceHandler;
import com.zpf.process.aidl.OnBindHost;
import com.zpf.process.manager.HostManagerImpl;

/**
 * @author Created by ZPF on 2021/4/22.
 */
public class HostService extends Service {
    private HostManagerImpl hostManager;
    private final OnBindHost.Stub bindStub = new OnBindHost.Stub() {

        @Override
        public IHostHandler onBind(int serviceId, IServiceHandler handler) throws RemoteException {
            if (hostManager == null) {
                hostManager = HostManagerImpl.get(HostService.this);
            }
            ProcessAdmin.updateServiceHandler(serviceId, handler);
            return hostManager;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return bindStub;
    }

}
