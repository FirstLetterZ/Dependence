package com.zpf.process.api;

import android.os.Bundle;

/**
 * @author Created by ZPF on 2021/4/8.
 */
public interface IEventHandler {
    boolean handleEvent(String event, Bundle params,IEventCallback callback);
}