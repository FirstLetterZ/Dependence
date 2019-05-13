package com.zpf.frame;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;

import com.zpf.api.IBackPressInterceptor;
import com.zpf.api.IViewLifecycle;
import com.zpf.api.OnActivityResultListener;
import com.zpf.api.OnPermissionResultListener;

/**
 * Created by ZPF on 2018/6/14.
 */
public interface IViewProcessor<C> extends IViewLifecycle, OnActivityResultListener, OnPermissionResultListener, IBackPressInterceptor {
    void runWithPermission(Runnable runnable, String... permissions);

    void runWithPermission(Runnable runnable, Runnable onLack, String... permissions);

    <T extends View> T bind(@IdRes int viewId, View.OnClickListener clickListener);

    <T extends View> T $(@IdRes int viewId);

    void onNewIntent(@NonNull Intent intent);

    @NonNull
    Bundle getParams();

    View getView();

    Context getContext();

    void onReceiveEvent(String action, Object... params);

    void setLinker(C linker);
}
