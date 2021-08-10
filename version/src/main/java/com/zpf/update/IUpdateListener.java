package com.zpf.update;

/**
 * @author Created by ZPF on 2021/3/29.
 */
public interface IUpdateListener {

    void onStart(String fileName);

    void onSuccess(FileVersionInfo versionInfo);

    void onChanged(String fileName, long total, long current);

    void onFail(String fileName, int code, String message);

    boolean alertUpdate(FileVersionInfo versionInfo, IAlertUpdateCallback callback);
}
