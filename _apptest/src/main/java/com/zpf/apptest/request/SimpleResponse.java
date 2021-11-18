package com.zpf.apptest.request;

import androidx.annotation.Nullable;

/**
 * @author Created by ZPF on 2021/6/24.
 */
public class SimpleResponse<T> implements IResponse<T> {
    private final boolean success;
    private final int code;
    private final String message;
    private final T data;

    public SimpleResponse(boolean success, int code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

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
