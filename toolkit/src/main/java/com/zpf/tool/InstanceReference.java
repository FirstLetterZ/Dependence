package com.zpf.tool;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

public class InstanceReference<T> {
    private volatile T mInstance = null;
    private final AtomicInteger instanceId = new AtomicInteger(0);
    private final Creator<T> creator;
    public InstanceReference(Creator<T> creator) {
        this.creator = creator;
    }

    @NotNull
    public T require() {
        T cache1 = mInstance;
        if (cache1 != null) {
            return cache1;
        }
        synchronized (this) {
            T cache2 = mInstance;
            if (cache2 == null) {
                cache2 = creator.newInstance(instanceId.incrementAndGet());
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

    public interface Creator<T> {
        T newInstance(int id);
    }
}