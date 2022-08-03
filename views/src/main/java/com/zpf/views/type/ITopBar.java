package com.zpf.views.type;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

/**
 * @author Created by ZPF on 2021/11/23.
 */
public interface ITopBar {
    View getStatusBar();

    IconText getLeftImage();

    IconText getLeftText();

    ViewGroup getLeftLayout();

    IconText getRightImage();

    IconText getRightText();

    ViewGroup getRightLayout();

    IconText getTitle();

    IconText getSubTitle();

    ViewGroup getTitleLayout();

    void setBottomLine(@Nullable Drawable drawable, int height);

    void setTitleBarHeight(int height);

    View getView();
}
