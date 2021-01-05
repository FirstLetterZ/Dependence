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
import com.zpf.api.OnActivityResultListener;
import com.zpf.api.OnPermissionResultListener;
import com.zpf.tool.permission.PermissionResultListener;

/**
 * Created by ZPF on 2018/6/14.
 */
public interface IViewProcessor extends IFullLifecycle, OnActivityResultListener,
        OnPermissionResultListener, IBackPressInterceptor, IViewStateListener, ILifecycleMonitor {
    void runWithPermission(Runnable runnable, String... permissions);

    void runWithPermission(PermissionResultListener resultListener, String... permissions);

    <T extends View> T bind(@IdRes int viewId, View.OnClickListener clickListener);

    <T extends View> T find(@IdRes int viewId);

    @NonNull
    Bundle getParams();

    View getView();

    Context getContext();

    void onReceiveEvent(IEvent<?> event);

    Activity getCurrentActivity();

    boolean initWindow(@NonNull Window window);
}
