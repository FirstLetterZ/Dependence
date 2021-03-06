package com.zpf.frame;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import android.view.View;
import android.view.Window;

import com.zpf.api.IBackPressInterceptor;
import com.zpf.api.IEvent;
import com.zpf.api.IFullLifecycle;
import com.zpf.api.IPermissionResult;
import com.zpf.api.OnActivityResultListener;
import com.zpf.api.OnPermissionResultListener;
import com.zpf.api.OnTouchKeyListener;

/**
 * Created by ZPF on 2018/6/14.
 */
public interface IViewProcessor extends IFullLifecycle, OnActivityResultListener, IViewStateListener,
        OnPermissionResultListener, IBackPressInterceptor, OnTouchKeyListener, ILifecycleMonitor {
    void runWithPermission(Runnable runnable, String... permissions);

    void runWithPermission(IPermissionResult resultListener, String... permissions);

    <T extends View> T bind(@IdRes int viewId, View.OnClickListener clickListener);

    <T extends View> T find(@IdRes int viewId);

    @NonNull
    Bundle getParams();

    View getView();

    Context getContext();

    boolean handleEvent(IEvent<?> event);

    void onReceiveLinker(IViewLinker linker);

    Activity getCurrentActivity();

    boolean initWindow(@NonNull Window window);
}
