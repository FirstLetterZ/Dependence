package com.zpf.api;

interface OnLoadingListener {
    void onLoading(boolean isUpdate, int code, String message);

    void onComplete(boolean isSuccess, int code, String message);
}
