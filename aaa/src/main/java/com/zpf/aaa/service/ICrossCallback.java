package com.zpf.aaa.service;

import android.content.ComponentName;
import android.os.Handler;

import androidx.annotation.Nullable;

public interface ICrossCallback extends Handler.Callback {
    void onBind(@Nullable ComponentName componentName);

    void onUnbind(@Nullable ComponentName componentName);
}
