package com.zpf.process.api;

import android.content.Context;
import android.os.Bundle;

/**
 * @author Created by ZPF on 2021/4/8.
 */
public interface IServiceStaff {

    void startToWork(Context appContext, int serviceId, String client, Bundle params);

    void stopAllWork(int serviceId);

}