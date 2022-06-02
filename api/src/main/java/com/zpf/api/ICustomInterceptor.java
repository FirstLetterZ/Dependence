package com.zpf.api;

import androidx.annotation.Nullable;

/**
 * 拦截返回键
 * Created by ZPF on 2018/6/13.
 */
public interface ICustomInterceptor<T> {
    boolean shouldIntercept(@Nullable T t);
}