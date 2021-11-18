package com.zpf.apptest.request;

import android.util.Pair;

import androidx.annotation.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Created by ZPF on 2021/6/23.
 */
public class RequestRecord<T> {
    private final Map<String, Pair<IRequestCall<T>, IResponseListener<T>>> recordMap;

    private volatile boolean clearing = false;

    public RequestRecord() {
        this.recordMap = new ConcurrentHashMap<>();
    }

    public void add(IRequestCall<T> requestCall, @Nullable IResponseListener<T> listener) {
        if (clearing || requestCall == null) {
            return;
        }
        String requestId = requestCall.id();
        if (requestId == null || requestId.length() == 0) {
            return;
        }
        recordMap.put(requestId, new Pair<>(requestCall, listener));
    }

    Pair<IRequestCall<T>, IResponseListener<T>> poll(IRequestCall<T> requestCall) {
        if (clearing || requestCall == null) {
            return null;
        }
        String requestId = requestCall.id();
        if (requestId == null || requestId.length() == 0) {
            return null;
        }
        return recordMap.remove(requestId);
    }

    public IRequestCall<T> query(String requestId) {
        if (clearing) {
            return null;
        }
        if (requestId == null || requestId.length() == 0) {
            return null;
        }
        Pair<IRequestCall<T>, IResponseListener<T>> pair = recordMap.get(requestId);
        if (pair == null) {
            return null;
        }
        return pair.first;
    }

    public IRequestCall<T> remove(IRequestCall<T> requestCall) {
        if (clearing || requestCall == null) {
            return null;
        }
        String requestId = requestCall.id();
        if (requestId == null || requestId.length() == 0) {
            return null;
        }
        Pair<IRequestCall<T>, IResponseListener<T>> pair = recordMap.remove(requestId);
        if (pair == null) {
            return null;
        }
        return pair.first;
    }

    public void cancelAll() {
        if (recordMap.size() == 0) {
            return;
        }
        clearing = true;
        IRequestCall<T> call;
        for (Map.Entry<String, Pair<IRequestCall<T>, IResponseListener<T>>> entry : recordMap.entrySet()) {
            call = entry.getValue().first;
            if (call != null) {
                call.cancel();
            }
        }
        recordMap.clear();
        clearing = false;
    }

}
