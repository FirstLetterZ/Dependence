package com.zpf.wheelpicker.listener;

import androidx.annotation.NonNull;

import com.zpf.wheelpicker.view.WheelView;

public interface OnBoundaryChangedListener {
    int DEF_UPPER_INDEX = Integer.MAX_VALUE;
    int DEF_LOWER_INDEX = 0;

    void onChanged(@NonNull WheelView wheelView, int currentIndex, int lowerBoundaryIndex, int upperBoundaryIndex);
}