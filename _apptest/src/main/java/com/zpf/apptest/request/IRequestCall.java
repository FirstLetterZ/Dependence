package com.zpf.apptest.request;

import androidx.annotation.Nullable;

/**
 * @author Created by ZPF on 2021/6/23.
 */
public interface IRequestCall<V> {
    void call(@Nullable IResponseListener<V> listener);

    String id();

    void cancel();

    boolean running();
}