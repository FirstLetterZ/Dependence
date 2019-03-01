package com.zpf.frame;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;

import com.zpf.api.LifecycleListener;

/**
 * Created by ZPF on 2018/6/14.
 */
public interface IViewProcessor extends LifecycleListener, ResultCallBackListener {
    void runWithPermission(Runnable runnable, String... permissions);

    void runWithPermission(Runnable runnable, Runnable onLack, String... permissions);

    <T extends View> T bind(@IdRes int viewId, View.OnClickListener clickListener);

    <T extends View> T $(@IdRes int viewId);

    IRootLayout getRootLayout();

    ITitleBar getTitleBar();

}
