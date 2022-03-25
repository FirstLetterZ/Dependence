package com.zpf.views.type;

import android.view.View;
import android.view.ViewGroup;

import com.zpf.views.StatusBar;

/**
 * @author Created by ZPF on 2021/11/23.
 */
public interface ITopBar {
    StatusBar getStatusBar();

    IconText getLeftImage();

    IconText getLeftText();

    ViewGroup getLeftLayout();

    IconText getRightImage();

    IconText getRightText();

    ViewGroup getRightLayout();

    IconText getTitle();

    IconText getSubTitle();

    ViewGroup getTitleLayout();

    View getView();
}
