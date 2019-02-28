package com.zpf.frame;


import com.zpf.api.ICustomWindow;
import com.zpf.api.IPermissionChecker;

/**
 * 替代activity与fragment
 * Created by ZPF on 2018/3/22.
 */
public interface ViewContainerInterface extends LifecycleMonitor, ActivityController
        , IPermissionChecker {

    IRootLayout getRootLayout();

    ITitleBar getTitleBar();

    void showLoading();

    void showLoading(String msg);

    boolean hideLoading();

    ICustomWindow getProgressDialog();

    Object invoke(String name, Object params);

}
