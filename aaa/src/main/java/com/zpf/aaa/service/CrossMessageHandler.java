package com.zpf.aaa.service;

import android.content.ComponentName;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.HashSet;

public class CrossMessageHandler implements ICrossCallback {

    private final HashMap<String, Messenger> messengerMap = new HashMap<>();
    private final HashSet<ICrossCallback> callbacks = new HashSet<>();
    private final Handler myHandler;
    public final Messenger myMessenger;
    public final ICrossMessenger crossMessenger;

    public CrossMessageHandler(String name, Looper looper) {
        myHandler = new Handler(looper, this);
        myMessenger = new Messenger(myHandler);
        crossMessenger = new DefCrossMessenger(name, myMessenger, messengerMap, callbacks);
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        boolean handled = false;
        String messengerName = msg.getData().getString(ICrossMessenger.DATA_KEY_MESSENGER_NAME);
        Messenger replyMessenger = msg.replyTo;
        if (replyMessenger != null) {
            updateMessenger(messengerName, replyMessenger);
        }
        for (ICrossCallback c : callbacks) {
            handled = c.handleMessage(msg);
            if (handled) {
                break;
            }
        }
        return handled;
    }

    @Override
    public void onBind(@Nullable ComponentName componentName) {
        for (ICrossCallback c : callbacks) {
            c.onBind(componentName);
        }
    }

    @Override
    public void onUnbind(@Nullable ComponentName componentName) {
        for (ICrossCallback c : callbacks) {
            c.onUnbind(componentName);
        }
    }

    public void updateMessenger(@Nullable String name, @Nullable Messenger messenger) {
        if (name == null) {
            return;
        }
        if (messenger == null) {
            messengerMap.remove(name);
        } else {
            messengerMap.put(name, messenger);
            crossMessenger.resend(name);
        }
    }

    public void onDestroy() {
        myHandler.removeCallbacksAndMessages(null);
    }

}
