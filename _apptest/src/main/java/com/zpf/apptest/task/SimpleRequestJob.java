package com.zpf.apptest.task;

import androidx.annotation.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleRequestJob<T> implements IRequestJob, ICallback<T> {

    private ICallback<T> callback;
    private AtomicBoolean canceled = new AtomicBoolean(false);
    private IResponse<T> responseCache;

    public SimpleRequestJob(ICallback<T> callback) {
        this.callback = callback;
    }

    @Override
    public void cancel() {
    }

    @Override
    public boolean isRunning() {
        return !canceled.get() && responseCache == null;
    }
    @Override
    public boolean isCanceled() {
        return canceled.get();
    }
    @Override
    public boolean isSuccess() {
        if (responseCache != null) {
            return responseCache.success();
        }
        return false;
    }
    @Nullable
    @Override
    public IResponse<T> response() {
        return responseCache;
    }

    @Override
    public void onResponse(IResponse<T> response) {
        responseCache = response;
        if (callback != null) {
            callback.onResponse(response);
        }
    }
}
