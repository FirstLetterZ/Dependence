package com.zpf.tool.fragment;

import android.view.View;

/**
 * Created by ZPF on 2018/10/24.
 */
public interface FragmentManagerInterface {
    void showFragment(int index);

    int getCurrentIndex();

    //主要子类:Activity,Fragment,Dialog
    View.OnCreateContextMenuListener getFragment(int index);
}
