package com.zpf.api;

import androidx.annotation.Nullable;

/**
 * 回调处理
 * Created by ZPF on 2018/11/2.
 */
public interface OnDataChangedListener<T> {
    void onChanged(@Nullable T oldValue, @Nullable T newValue);
}
