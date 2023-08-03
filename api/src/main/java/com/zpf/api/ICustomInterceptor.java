package com.zpf.api;

import androidx.annotation.Nullable;

public interface ICustomInterceptor<T> {
    boolean shouldIntercept(String key, @Nullable T value);
}