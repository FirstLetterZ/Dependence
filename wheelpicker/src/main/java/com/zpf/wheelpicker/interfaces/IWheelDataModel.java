package com.zpf.wheelpicker.interfaces;

import com.zpf.wheelpicker.adapter.WheelAdapter;
import com.zpf.wheelpicker.listener.OnBoundaryChangedListener;
import com.zpf.wheelpicker.listener.OnItemSelectedListener;

public interface IWheelDataModel<T> {
    WheelAdapter<?> getAdapter(int position);

    int getListSize();

    OnItemSelectedListener getSelectedListener(int position);

    OnBoundaryChangedListener getBoundaryListener(int position);

    boolean hasBoundary();

    int getSelectIndex(int position);

    void setBoundary(T start, T end);

    void setInitData(T data);

    T getSelectData();

    void refreshDataList();

    void setLinkageManager(ILinkageManager manager);
}
