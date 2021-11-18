package com.zpf.views.type;

import android.view.ViewGroup;

/**
 * Created by ZPF on 2018/6/13.
 */
public interface ITitleBar {

    IconText getLeftImage();

    IconText getLeftText();

    ViewGroup getLeftLayout();

    IconText getRightImage();

    IconText getRightText();

    ViewGroup getRightLayout();

    IconText getTitle();

    IconText getSubTitle();

    ViewGroup getTitleLayout();

    ViewGroup getLayout();
}
