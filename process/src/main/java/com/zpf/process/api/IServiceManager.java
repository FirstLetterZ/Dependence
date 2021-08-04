package com.zpf.process.api;

import android.os.Bundle;

/**
 * @author Created by ZPF on 2021/4/8.
 */
public interface IServiceManager extends IManager {

    IServiceManager setServiceListener(IServiceListener listener);

    IServiceManager setServiceStaff(IServiceStaff staff);

    void sendToHost(String event, Bundle params, IEventCallback eventCallback);

    int getServiceId();
}