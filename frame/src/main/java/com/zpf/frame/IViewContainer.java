package com.zpf.frame;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.zpf.api.IEvent;
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

    boolean sendEvenToView(@NonNull IEvent event);

    int getContainerType();

    IViewContainer getParentContainer();

    void bindView(IViewProcessor processor);

    void unbindView();

    IViewProcessor getViewProcessor();

    INavigator<Class<? extends IViewProcessor>> getNavigator();
}