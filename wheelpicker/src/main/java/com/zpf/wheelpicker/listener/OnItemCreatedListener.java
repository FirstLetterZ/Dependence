package com.zpf.wheelpicker.listener;

import com.zpf.wheelpicker.view.WheelView;

public interface OnItemCreatedListener {
    void onItemCreated(WheelView wheelView, int position);

    void onFinishBuildChildren(int size);
}
