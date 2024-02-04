package com.zpf.aaa.service;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class CrossMessenger {
    private static final HashMap<String, CrossMessageClient> clientMap = new HashMap<>();
    protected static ICrossMessenger serviceMessenger;
    public static ICrossMessenger getServiceMessenger() {
        return serviceMessenger;
    }

    private CrossMessageHandler crossHandler;
    public static ICrossMessenger getClientMessenger(@NonNull Context context, @NonNull String name) {
        CrossMessageClient client = clientMap.get(name);
        if (client == null) {
            client = new CrossMessageClient(context, name);
        }
        return client.clientMessenger;
    }

    public static void closeClient(@NonNull String name) {
        CrossMessageClient client = clientMap.remove(name);
        if (client != null) {
            client.onDestroy();
        }
    }
}
