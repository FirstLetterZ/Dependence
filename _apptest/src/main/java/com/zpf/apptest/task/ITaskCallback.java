package com.zpf.apptest.task;

/**
 * @author Created by ZPF on 2021/10/16.
 */
public interface ITaskCallback {
    void onResult(int type, Object params, int code, Object data, String message);
}
