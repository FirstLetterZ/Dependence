package com.zpf.apptest.request;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;

/**
 * @author Created by ZPF on 2021/6/23.
 */
public class Requester<T> implements IResponseListener<T> {
    private final IRequestCreator<T> requestCreator;
    private final RequestRecord<T> record;
    private IRequestCache cacheManager;

    public Requester(IRequestCreator<T> requestCreator) {
        this.requestCreator = requestCreator;
        record = new RequestRecord<T>();
    }

    public Requester(IRequestCreator<T> requestCreator, RequestRecord<T> record) {
        this.requestCreator = requestCreator;
        this.record = record;
    }

    public void setCacheManager(IRequestCache cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void request(@NonNull JSONObject params, @Nullable final IResponseListener<T> listener) {
        if (listener instanceof IRequestCacheListener) {
            String cacheInfo = null;
            if (cacheManager != null) {
                cacheInfo = cacheManager.queryCache(params);
            }
            if (cacheInfo != null && ((IRequestCacheListener) listener).onFindCache(cacheInfo)) {
                return;
            }
        }
        final IRequestCall<T> requestCall = requestCreator.create(params);
        record.add(requestCall, listener);
        requestCall.call(new IResponseListener<T>() {
            @Override
            public void onResponse(@NonNull IRequestCall<T> call, @NonNull IResponse<T> response) {
                if (listener != null) {
                    listener.onResponse(call, response);
                }
                record.remove(requestCall);
            }
        });
    }

    public RequestRecord<T> getRecord() {
        return record;
    }

    @Override
    public void onResponse(@NonNull IRequestCall<T> call, @NonNull IResponse<T> response) {
        Pair<IRequestCall<T>, IResponseListener<T>> pair = record.poll(call);
        if (pair == null) {
            return;
        }
        IResponseListener<T> listener = pair.second;
        if (listener != null) {
            listener.onResponse(call, response);
        }
    }
}