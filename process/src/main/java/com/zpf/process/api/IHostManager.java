package com.zpf.process.api;

import android.os.Bundle;

/**
 * @author Created by ZPF on 2021/4/8.
 */
public interface IHostManager extends IManager {
    void sendToService(String clientId, String event, Bundle params, IEventCallback eventCallback);
}