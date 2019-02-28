package com.zpf.frame;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
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

    void setTitleBar(@NonNull ITitleBar titleBar);

    ITitleBar getTitleBar();

    void setTopViewBackground(Drawable drawable);

    void setTopViewBackground(@ColorInt int color);

    void setContentView(@NonNull View view);

    void setContentView(@NonNull LayoutInflater inflater, int layoutId);

    ViewGroup getLayout();

    FrameLayout getContentLayout();
}
