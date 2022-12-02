package com.zpf.wheelpicker.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.wheelpicker.adapter.ListWheelAdapter;
import com.zpf.wheelpicker.adapter.WheelAdapter;
import com.zpf.wheelpicker.interfaces.IStyledViewData;
import com.zpf.wheelpicker.listener.OnBoundaryChangedListener;
import com.zpf.wheelpicker.listener.OnColumnChangedListener;
import com.zpf.wheelpicker.listener.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class WheelColumnHolder<T> {
    public final int column;
    @NonNull
    public final WheelAdapter<T> adapter;
    @NonNull
    public final OnItemSelectedListener itemSelectedListener;
    @NonNull
    public final OnBoundaryChangedListener boundaryChangedListener;
    private OnColumnChangedListener<T> columnChangedListener;
    private int itemBoundaryState = 999;
    @Nullable
    public T selectData;
    public int selectPosition = -1;
    private int lowerBoundaryIndex = -1;
    private int upperBoundaryIndex = -1;
    @Nullable
    public WheelItemStyle errorStyle;

    public WheelColumnHolder(int column) {
        this.column = column;
        this.adapter = new ListWheelAdapter<>();
        itemSelectedListener = (view, itemIndex) -> {
            T oldValue = selectData;
            T newValue;
            int newPosition;
            if (itemIndex < 0 || itemIndex >= adapter.getItemsCount()) {
                newValue = null;
                newPosition = -1;
            } else {
                newValue = adapter.getItem(itemIndex);
                newPosition = itemIndex;
            }
            selectPosition = newPosition;
            selectData = newValue;
            if (columnChangedListener != null) {
                columnChangedListener.onColumnDataChanged(column, oldValue, newValue);
            }
        };
        boundaryChangedListener = (wheelView, currentIndex, lowerBoundaryIndex, upperBoundaryIndex) -> {
            WheelColumnHolder.this.lowerBoundaryIndex = lowerBoundaryIndex;
            WheelColumnHolder.this.upperBoundaryIndex = upperBoundaryIndex;
            if (refreshItemStyle(currentIndex, lowerBoundaryIndex, upperBoundaryIndex)) {
                wheelView.invalidate();
            }
        };
    }

    public void updateColumnBoundary(int lowerBoundaryIndex, int upperBoundaryIndex, boolean shouldCropData) {
        this.lowerBoundaryIndex = lowerBoundaryIndex;
        this.upperBoundaryIndex = upperBoundaryIndex;
        if (!shouldCropData) {
            return;
        }
        List<T> cropList;
        if (upperBoundaryIndex > lowerBoundaryIndex) {
            cropList = new ArrayList<>();
            for (int i = 0; i < adapter.getItemsCount(); i++) {
                if (i >= lowerBoundaryIndex && i <= upperBoundaryIndex) {
                    cropList.add(adapter.getItem(i));
                }
            }
        } else {
            cropList = null;
        }
        changeDataSource(cropList);
    }

    public int updateSelect(@Nullable T select, boolean isRestoreItem) {
        int selectIndex;
        int adapterSize = adapter.getItemsCount();
        if (adapterSize == 0) {
            selectIndex = -1;
        } else {
            if (select != null) {
                selectIndex = adapter.indexOf(select);
            } else if (isRestoreItem) {
                selectIndex = 0;
            } else {
                selectIndex = selectPosition;
            }
            if (selectIndex < 0) {
                selectIndex = 0;
            } else if (selectIndex >= adapterSize) {
                selectIndex = selectIndex - 1;
            }
        }
        selectPosition = selectIndex;
        selectData = adapter.getItem(selectIndex);
        refreshItemStyle(selectIndex, lowerBoundaryIndex, upperBoundaryIndex);
        return selectIndex;
    }

    public boolean isOnLowerBoundary() {
        return selectPosition >= 0 && selectPosition == lowerBoundaryIndex;
    }

    public boolean isOnUpperBoundary() {
        return selectPosition >= 0 && selectPosition == upperBoundaryIndex;
    }

    public void changeDataSource(@Nullable List<? extends T> source) {
        ((ListWheelAdapter<T>) adapter).changeDataSource(source);
    }

    public void setColumnChangedListener(OnColumnChangedListener<T> columnChangedListener) {
        this.columnChangedListener = columnChangedListener;
    }

    public void rest() {
        itemBoundaryState = 999;
    }

    private boolean refreshItemStyle(int currentIndex, int lowerBoundaryIndex, int upperBoundaryIndex) {
        if (adapter.getItemsCount() == 0) {
            itemBoundaryState = 999;
            return false;
        }
        int newState;
        if (currentIndex >= upperBoundaryIndex) {
            newState = 1;
        } else if (currentIndex <= lowerBoundaryIndex) {
            newState = -1;
        } else {
            newState = 0;
        }
        if (newState == itemBoundaryState) {
            return false;
        }
        itemBoundaryState = newState;
        WheelItemStyle itemStyle;
        for (int i = 0; i < adapter.getItemsCount(); i++) {
            T item = adapter.getItem(i);
            if (item instanceof IStyledViewData) {
                if (i < lowerBoundaryIndex || i > upperBoundaryIndex) {
                    itemStyle = errorStyle;
                    if (itemStyle == null) {
                        itemStyle = WheelItemStyle.defErrorStyle;
                    }
                } else {
                    itemStyle = null;
                }
                ((IStyledViewData) item).setItemStyle(itemStyle);
            }
        }
        return true;
    }
}