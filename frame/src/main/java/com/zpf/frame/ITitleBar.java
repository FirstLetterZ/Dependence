package com.zpf.frame;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ZPF on 2018/6/13.
 */
public interface ITitleBar {

    View getLeftImage();

    View getLeftText();

    ViewGroup getLeftLayout();

    View getRightImage();

    View getRightText();

    ViewGroup getRightLayout();

    View getTitle();

    View getSubTitle();

    ViewGroup getTitleLayout();

    ViewGroup getLayout();
}
