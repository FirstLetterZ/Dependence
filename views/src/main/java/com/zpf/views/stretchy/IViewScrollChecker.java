package com.zpf.views.stretchy;

import android.view.View;

public interface IViewScrollChecker {
    boolean canScrollVertically(View target, int direction);

    boolean canScrollHorizontally(View target, int direction);
}
