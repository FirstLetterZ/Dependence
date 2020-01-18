package com.zpf.api;

interface OnStateChangedListener<T> {
    void onStateChanged(boolean loading, int code, String msg, T data);
}
