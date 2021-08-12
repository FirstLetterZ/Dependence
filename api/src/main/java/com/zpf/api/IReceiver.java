package com.zpf.api;

import androidx.annotation.NonNull;

/**
 * @author Created by ZPF on 2021/8/12.
 */
public interface IReceiver<T> {
    @NonNull
    String name();

    void onReceive(@NonNull T t,@NonNull ITransRecord record);
}
