package com.zpf.api;

import android.view.View;

/**
 * Created by ZPF on 2019/2/27.
 */
public interface ICustomWindow extends INeedManage {
    void show();

    void dismiss();

    boolean isShowing();

    void setContentView(View view);
}
