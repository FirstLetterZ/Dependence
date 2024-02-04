package com.zpf.aaa.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;

public class CrossMessageService extends Service {
    private CrossMessageHandler crossHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        crossHandler = new CrossMessageHandler(ICrossMessenger.SERVICE_MESSAGE_NAME, Looper.getMainLooper());
        setServiceMessenger(crossHandler.crossMessenger);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (crossHandler != null) {
            ComponentName componentName = intent.getComponent();
            crossHandler.onBind(componentName);
            return crossHandler.myMessenger.getBinder();
        }
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (crossHandler != null) {
            ComponentName componentName = intent.getComponent();
            crossHandler.onUnbind(componentName);
        }
        return false;
    }

    @Override
    public void onDestroy() {
        setServiceMessenger(null);
        super.onDestroy();
    }

    protected void setServiceMessenger(ICrossMessenger messenger) {
        CrossMessenger.serviceMessenger = messenger;
    }
}