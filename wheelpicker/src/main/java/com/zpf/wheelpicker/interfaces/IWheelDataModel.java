package com.zpf.wheelpicker.interfaces;

import androidx.annotation.Nullable;

import com.zpf.wheelpicker.adapter.WheelAdapter;
import com.zpf.wheelpicker.listener.OnBoundaryChangedListener;
import com.zpf.wheelpicker.listener.OnItemSelectedListener;

public interface IWheelDataModel<T> {
    WheelAdapter<?> getAdapter(int column);

    int getSize();

    @Nullable
    OnItemSelectedListener getSelectedListener(int column);

    @Nullable
    OnBoundaryChangedListener getBoundaryListener(int column);

    boolean overstepRollback();

    int getSelectIndex(int column);

    void setBoundary(@Nullable T start, @Nullable T end);

    void setInitData(@Nullable T data);

    @Nullable
    T getSelectData();

    void refreshDataList();

    void setLinkageManager(@Nullable ILinkageViewManager manager);
}