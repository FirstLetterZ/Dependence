package com.zpf.aaa.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;

public class CrossMessageClient implements ServiceConnection {
    public final ICrossMessenger clientMessenger;
    private final CrossMessageHandler crossHandler;
    private final Context appContext;

    public CrossMessageClient(Context context, String name) {
        appContext = context.getApplicationContext();
        crossHandler = new CrossMessageHandler(name, Looper.getMainLooper());
        clientMessenger = crossHandler.crossMessenger;
        Intent intent = new Intent(appContext, CrossMessageService.class);
        context.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Messenger serviceMessenger = new Messenger(service);
        crossHandler.updateMessenger(ICrossMessenger.SERVICE_MESSAGE_NAME, serviceMessenger);
        crossHandler.onBind(name);
    }
    @Override
    public void onServiceDisconnected(ComponentName name) {
        crossHandler.onUnbind(name);
    }

    public void onDestroy() {
        appContext.unbindService(this);
        crossHandler.onDestroy();
    }
}
