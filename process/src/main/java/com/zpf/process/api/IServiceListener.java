package com.zpf.process.api;

import android.app.Service;

/**
 * @author Created by ZPF on 2021/4/8.
 */
public interface IServiceListener {
    void onServiceCreate(Service service, int serviceId);

    void onServiceDestroy(Service service, int serviceId);
}
