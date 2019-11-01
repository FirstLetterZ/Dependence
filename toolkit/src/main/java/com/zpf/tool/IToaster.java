package com.zpf.tool;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public interface IToaster {
    View getToastView();

    ViewGroup getLayout();

    WindowManager.LayoutParams getWindowParams();

    void showToast(CharSequence text);

    void onDismiss();

    boolean isUsable();
}