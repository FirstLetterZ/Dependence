package com.zpf.aaa.service;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DefCrossMessenger implements ICrossMessenger {
    private final HashSet<ICrossCallback> callbacks;
    private final HashMap<String, Messenger> messengers;
    private final LinkedList<CrossMessageCache> failCaches = new LinkedList<>();
    public int maxFailSize = 8;
    private final String name;
    private final Messenger sender;

    public DefCrossMessenger(String messengerName, Messenger messenger, HashMap<String, Messenger> messengerMap, HashSet<ICrossCallback> messageCallbacks) {
        name = messengerName;
        sender = messenger;
        messengers = messengerMap;
        callbacks = messageCallbacks;
    }

    @Override
    public boolean send(@NonNull String targetName, int what, @Nullable Bundle extraData) {
        Message message = Message.obtain();
        message.what = what;
        message.setData(extraData);
        return send(targetName, message);
    }

    @Override
    public boolean send(@NonNull String target, @NonNull Message message) {
        Messenger replayMessenger = messengers.get(target);
        if (replayMessenger == null) {
            recordFailMessage(new CrossMessageCache(target, message));
            return false;
        }
        message.getData().putString(DATA_KEY_MESSENGER_NAME, name);
        message.replyTo = sender;
        try {
            replayMessenger.send(message);
            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
            recordFailMessage(new CrossMessageCache(target, message));
            return false;
        }
    }

    @Override
    public void addCallback(ICrossCallback crossCallback) {
        callbacks.add(crossCallback);
    }
    @Override
    public void removeCallback(ICrossCallback crossCallback) {
        callbacks.remove(crossCallback);
    }

    @Override
    public boolean resend(@NonNull String target) {
        if (failCaches.size() == 0) {
            return true;
        }
        Messenger replayMessenger = messengers.get(target);
        if (replayMessenger == null) {
            return false;
        }
        List<CrossMessageCache> resendList = new ArrayList<>(maxFailSize);
        Iterator<CrossMessageCache> iterator = failCaches.iterator();
        CrossMessageCache cacheItem;
        while (iterator.hasNext()) {
            cacheItem = iterator.next();
            if (target.equals(cacheItem.target)) {
                iterator.remove();
                resendList.add(cacheItem);
            }
        }
        if (resendList.size() == 0) {
            return true;
        }
        boolean allSuccess = true;
        for (CrossMessageCache cache : resendList) {
            try {
                replayMessenger.send(cache.message);
            } catch (RemoteException e) {
                e.printStackTrace();
                allSuccess = false;
            }
        }
        return allSuccess;
    }

    private void recordFailMessage(CrossMessageCache cache) {
        failCaches.add(cache);
        while (failCaches.size() > maxFailSize) {
            failCaches.pollFirst();
        }
    }

}
