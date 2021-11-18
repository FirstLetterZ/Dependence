package com.zpf.apptest.request;

import androidx.annotation.Nullable;

/**
 * @author Created by ZPF on 2021/6/23.
 */
public interface IRequestCall<T> {
    void call(@Nullable IResponseListener<T> listener);

    String id();

    void cancel();

    boolean running();
}