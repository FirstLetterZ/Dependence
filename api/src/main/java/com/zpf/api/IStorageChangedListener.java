package com.zpf.api;

/**
 * Created by ZPF on 2021/2/1.
 */
public interface IStorageChangedListener<T> {
    void onStorageChanged(T key, Object value);

    void onStorageClear();
}