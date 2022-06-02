package com.zpf.views.type;

import android.view.View;
import android.view.ViewGroup;

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

    View getView();
}
