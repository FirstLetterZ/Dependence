package com.zpf.apptest.task;

public interface ICallback<T> {
    void onResponse(IResponse<T> response);
}
