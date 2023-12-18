package com.zpf.views.stretchy;

import android.view.View;

public interface IViewStateListener {
    void onStateChanged(View target, int left, int top, int right, int bottom);
}
