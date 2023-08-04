package com.zpf.api;

import androidx.annotation.NonNull;

/**
 * Created by ZPF on 2019/1/24.
 */
public interface IStorageManager<N> {

    boolean save(N name, Object value);

    boolean remove(N name);

    <T> T find(N key, @NonNull Class<T> cls);

    <T> T find(N key, @NonNull T defValue);

    void clear();

    IStorageQueue<N> createQueue();
}