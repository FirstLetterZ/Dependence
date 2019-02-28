package com.zpf.frame;

import android.support.annotation.IdRes;
import android.view.View;

import com.zpf.api.LifecycleListener;

/**
 * Created by ZPF on 2018/6/14.
 */
public interface ContainerProcessorInterface extends LifecycleListener, ResultCallBackListener {
    void runWithPermission(Runnable runnable, String... permissions);

    void runWithPermission(Runnable runnable, Runnable onLack, String... permissions);

    <T extends View> T bind(@IdRes int viewId, View.OnClickListener clickListener);

}
