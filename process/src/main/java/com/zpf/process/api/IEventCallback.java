package com.zpf.process.api;

import android.os.Bundle;

/**
 * @author Created by ZPF on 2021/4/9.
 */
public interface IEventCallback {
    void callback(boolean handled, Bundle bundle);
}