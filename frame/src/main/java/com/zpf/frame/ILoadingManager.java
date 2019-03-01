package com.zpf.frame;

import android.view.View;

/**
 * Created by ZPF on 2019/3/1.
 */

public interface ILoadingManager {
    void showLoading();

    void showLoading(String msg);

    boolean hideLoading();

    View getLoadingView();
}
