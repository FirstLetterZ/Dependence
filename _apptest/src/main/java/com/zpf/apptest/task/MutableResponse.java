package com.zpf.apptest.task;

import androidx.annotation.Nullable;

public class MutableResponse<T> implements IResponse<T> {
    public boolean success;
    public int code;
    @Nullable
    public String message;
    @Nullable
    public T data;

    @Override
    public boolean success() {
        return success;
    }
    @Override
    public int code() {
        return code;
    }
    @Nullable
    @Override
    public String message() {
        return message;
    }
    @Nullable
    @Override
    public T data() {
        return data;
    }
}
