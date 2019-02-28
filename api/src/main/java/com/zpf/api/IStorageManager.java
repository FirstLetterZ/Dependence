package com.zpf.api;

import android.support.annotation.NonNull;

/**
 * Created by ZPF on 2019/1/24.
 */

public interface IStorageManager<N> {

    IStorageManager save(N name, Object value);

    <T> T find(N key, @NonNull Class<T> cls);

    <T> T find(N key, @NonNull T defValue);

    void clearAll();

    StorageQueueInterface<N> createQueue();
}
