package com.zpf.tool;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

abstract public class InstanceReference<T> {
    private volatile T mInstance = null;
    private final AtomicInteger instanceId = new AtomicInteger(0);

    protected abstract T newInstance(int id);
    protected boolean isValid(T t) {
        return t != null;
    }

    @NotNull
    public T require() {
        T cache1 = mInstance;
        if (isValid(cache1)) {
            return cache1;
        }
        synchronized (this) {
            T cache2 = mInstance;
            if (cache2 == null) {
                cache2 = newInstance(instanceId.incrementAndGet());
                mInstance = cache2;
            }
            return cache2;
        }
    }

    @Nullable
    public T get() {
        return mInstance;
    }

    @Nullable
    public T clear() {
        T cache = mInstance;
        mInstance = null;
        return cache;
    }
}