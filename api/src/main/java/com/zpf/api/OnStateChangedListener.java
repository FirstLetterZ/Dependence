package com.zpf.api;

public interface OnStateChangedListener<T> {
    void onStateChanged(boolean loading, int code, String msg, T data);
}
