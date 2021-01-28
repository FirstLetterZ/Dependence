package com.zpf.wheelpicker.interfaces;

import androidx.annotation.NonNull;

import com.zpf.wheelpicker.listener.OnItemCreatedListener;
import com.zpf.wheelpicker.view.WheelsLayout;

public interface IWheelLayoutHelper<T> extends OnItemCreatedListener {
    void bindView(@NonNull WheelsLayout wheelsLayout);

    void setModel(@NonNull IWheelDataModel<T> dataModel);

    T getSelectResult();

    boolean isBuildFinish();
}
