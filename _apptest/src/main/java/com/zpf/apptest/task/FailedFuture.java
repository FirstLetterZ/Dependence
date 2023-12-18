package com.zpf.apptest.task;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FailedFuture<T> implements Future<IResponse<T>> {
    public final IResponse<T> response;
    public FailedFuture(IResponse<T> response) {
        this.response = response;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }
    @Override
    public boolean isCancelled() {
        return false;
    }
    @Override
    public boolean isDone() {
        return true;
    }
    @Override
    public IResponse<T> get() {
        return response;
    }
    @Override
    public IResponse<T> get(long timeout, TimeUnit unit) {
        return response;
    }
}
