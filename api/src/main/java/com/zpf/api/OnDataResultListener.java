package com.zpf.api;

import android.support.annotation.Nullable;

/**
 * 回调处理
 * Created by ZPF on 2018/11/2.
 */
public interface OnDataResultListener<T> {
    void onResult(boolean success, @Nullable T data);
}
