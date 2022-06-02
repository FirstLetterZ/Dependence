package com.zpf.api;

import androidx.annotation.Nullable;

public interface IDataCallback<T> {
    void onResult(int code, @Nullable T data);
}