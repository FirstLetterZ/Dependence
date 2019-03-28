package com.zpf.frame;

import android.view.View;
import android.view.ViewGroup;

import com.zpf.api.IconText;

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
