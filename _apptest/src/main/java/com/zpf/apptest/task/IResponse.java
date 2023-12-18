package com.zpf.apptest.task;

import androidx.annotation.Nullable;

public interface IResponse<T> {

    boolean success();

    int code();

    @Nullable
    String message();

    @Nullable
    T data();
}