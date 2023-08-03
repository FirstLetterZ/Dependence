package com.zpf.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by ZPF on 2019/1/24.
 */
public interface IStorageManager<N> {

    IStorageManager<N> save(N name, Object value);

    @Nullable
    Object remove(N name);

    <T> T find(N key, @NonNull Class<T> cls);

    <T> T find(N key, @NonNull T defValue);

    void clear();

    IStorageQueue<N> createQueue();
}