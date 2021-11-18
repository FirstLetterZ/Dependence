package com.zpf.apptest.tst;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.api.IBackPressInterceptor;
import com.zpf.api.IFullLifecycle;
import com.zpf.api.OnActivityResultListener;
import com.zpf.api.OnPermissionResultListener;
import com.zpf.api.OnTouchKeyListener;
import com.zpf.views.type.ITitleBar;

import java.lang.reflect.Type;

/**
 * @author Created by ZPF on 2021/11/12.
 */
public interface IContainerFiller extends IFullLifecycle, OnActivityResultListener,
        OnPermissionResultListener, IBackPressInterceptor, OnTouchKeyListener {
    void onParamChanged(Bundle newParams);

    void onVisibleChanged(boolean visible);

    void onStateChanged(int stateCode);

    boolean addListener(Object listener, @Nullable Type listenerClass);

    boolean removeListener(Object listener, @Nullable Type listenerClass);

    <T extends View> T bind(@IdRes int viewId, View.OnClickListener clickListener);

    <T extends View> T find(@IdRes int viewId);

    @NonNull
    Bundle getParams();

    Activity getCurrentActivity();

    boolean initWindow(@NonNull Window window);

    View getView();

    IViewContainer getContainer();

}
