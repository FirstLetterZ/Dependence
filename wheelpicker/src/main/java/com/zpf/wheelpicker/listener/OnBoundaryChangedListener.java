package com.zpf.wheelpicker.listener;

import com.zpf.wheelpicker.view.WheelView;

public interface OnBoundaryChangedListener {
    void onChanged(WheelView wheelView, int currentIndex, int lowerBoundary, int upperBoundary);
}
