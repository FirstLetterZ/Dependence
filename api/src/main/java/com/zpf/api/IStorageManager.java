package com.zpf.api;

import androidx.annotation.NonNull;

/**
 * Created by ZPF on 2019/1/24.
 */

public interface IStorageManager<N> {

    IStorageManager<N> save(N name, Object value);

    <T> T find(N key, @NonNull Class<T> cls);

    <T> T find(N key, @NonNull T defValue);

    void clearAll();

    IStorageQueue<N> createQueue();
}
