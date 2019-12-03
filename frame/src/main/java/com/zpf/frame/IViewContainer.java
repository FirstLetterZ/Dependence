package com.zpf.frame;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zpf.api.IPermissionChecker;
import com.zpf.tool.config.stack.IStackItemPrototype;

/**
 * 替代activity与fragment
 * Created by ZPF on 2018/3/22.
 */
public interface IViewContainer extends ILifecycleMonitor, IActivityController
        , IPermissionChecker, ILoadingManager, IStackItemPrototype {

    Object invoke(String name, Object params);

    void setLoadingManager(ILoadingManager loadingManager);

    @NonNull
    Bundle getParams();

    int getContainerType();

    @Nullable
    IViewContainer getParentContainer();

    @Nullable
    IViewProcessor getViewProcessor();

    INavigator<Class<? extends IViewProcessor>> getNavigator();
}