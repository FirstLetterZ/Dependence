package com.zpf.frame;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;

import com.zpf.api.LifecycleListener;

/**
 * Created by ZPF on 2018/6/14.
 */
public interface IViewProcessor<C> extends LifecycleListener, ResultCallBackListener {
    void runWithPermission(Runnable runnable, String... permissions);

    void runWithPermission(Runnable runnable, Runnable onLack, String... permissions);

    <T extends View> T bind(@IdRes int viewId, View.OnClickListener clickListener);

    <T extends View> T $(@IdRes int viewId);

    void navigate(Class<? extends IViewProcessor> cls);

    void navigate(Class<? extends IViewProcessor> cls, Bundle params);

    void navigate(Class<? extends IViewProcessor> cls, Bundle params, int requestCode);

    @NonNull
    Bundle getParams();

    View getView();

    Context getContext();

    void onReceiveEvent(String action, Object... params);

    void setConnector(C connector);
}
