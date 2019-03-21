package com.zpf.frame;

import android.os.Bundle;

import com.zpf.api.IPermissionChecker;

/**
 * 替代activity与fragment
 * Created by ZPF on 2018/3/22.
 */
public interface IViewContainer extends ILifecycleMonitor, IActivityController
        , IPermissionChecker, ILoadingManager {

    Object invoke(String name, Object params);

    void setLoadingManager(ILoadingManager loadingManager);

    Bundle getParams();

    boolean sendEvenToView(String action, Object... params);

    int getContainerType();

    IViewContainer getParentContainer();

    void bindView(IViewProcessor processor);

    void unbindView(IViewProcessor processor);
}