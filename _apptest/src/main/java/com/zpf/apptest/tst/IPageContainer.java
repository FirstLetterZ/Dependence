package com.zpf.apptest.tst;

import android.view.View;
import android.widget.LinearLayout;

import com.zpf.views.type.ITitleBar;

/**
 * @author Created by ZPF on 2021/11/12.
 */
public interface IPageContainer extends IViewContainer {

    View getStatusBar();

    ITitleBar getTitleBar();

    View getContentView();

}
