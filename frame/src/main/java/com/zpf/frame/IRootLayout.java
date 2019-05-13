package com.zpf.frame;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by ZPF on 2018/6/14.
 */

public interface IRootLayout {

    View getStatusBar();

    ITitleBar getTitleBar();

    IShadowLine getShadowLine();

    ITopLayout getTopLayout();

    void changeTitleBar(@NonNull ITitleBar titleBar);

    void setContentView(@NonNull View view);

    void setContentView(@NonNull LayoutInflater inflater, int layoutId);

    ViewGroup getLayout();

    FrameLayout getContentLayout();
}
