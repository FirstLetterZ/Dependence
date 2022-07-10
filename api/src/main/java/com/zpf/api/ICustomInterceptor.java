package com.zpf.api;

import androidx.annotation.Nullable;

public interface ICustomInterceptor {
    boolean shouldIntercept(String key, @Nullable Object value);
}