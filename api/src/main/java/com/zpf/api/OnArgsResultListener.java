package com.zpf.api;

/**
 * 回调处理
 * Created by ZPF on 2018/11/2.
 */
public interface OnArgsResultListener {
    void onResult(boolean success, Object... args);
}
