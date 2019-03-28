package com.zpf.frame;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ZPF on 2019/3/28.
 */

public interface ITopLayout {

    View getStatusBar();

    ITitleBar getTitleBar();

    ViewGroup getLayout();

}
