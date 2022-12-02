package com.zpf.wheelpicker.listener;

import androidx.annotation.NonNull;

import com.zpf.wheelpicker.view.WheelView;

public interface OnItemSelectedListener {
    void onItemSelected(@NonNull WheelView view, int itemIndex);
}