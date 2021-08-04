package com.zpf.process.api;

/**
 * @author Created by ZPF on 2021/6/29.
 */
public interface IProcessInitCallback {
    void onHostInit(IHostManager manager);

    void onServiceInit(IServiceManager manager);
}
