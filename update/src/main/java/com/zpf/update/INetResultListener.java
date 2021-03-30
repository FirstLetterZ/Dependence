package com.zpf.update;

/**
 * @author Created by ZPF on 2021/3/29.
 */
public interface INetResultListener {
    void onFail(String fileName, int code,String message);

    void onSuccess(FileVersionInfo versionInfo);
}