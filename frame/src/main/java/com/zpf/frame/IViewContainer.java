package com.zpf.frame;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 替代activity与fragment
 * Created by ZPF on 2018/3/22.
 */
public interface IViewContainer extends ILifecycleMonitor, IActivityController
        , IPermissionChecker, ILoadingManager {

    Object invoke(String name, Object params);

    void setLoadingManager(ILoadingManager loadingManager);

    @NonNull
    Bundle getParams();

    int getContainerType();

    boolean setProcessorLinker(IViewLinker linker);

    @Nullable
    IViewContainer getParentContainer();

    @Nullable
    IViewProcessor getViewProcessor();

    INavigator<Class<? extends IViewProcessor>> getNavigator();
}