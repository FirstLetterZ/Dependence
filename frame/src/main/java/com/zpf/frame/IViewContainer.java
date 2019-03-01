package com.zpf.frame;

import android.os.Bundle;

import com.zpf.api.IPermissionChecker;

/**
 * 替代activity与fragment
 * Created by ZPF on 2018/3/22.
 */
public interface IViewContainer extends ILifecycleMonitor, IActivityController
        , IPermissionChecker, ILoadingManager {
    void navigate(Class<? extends IViewProcessor> cls);

    void navigate(Class<? extends IViewProcessor> cls, Bundle params);

    void navigate(Class<? extends IViewProcessor> cls, Bundle params, int requestCode);

    Object invoke(String name, Object params);
}