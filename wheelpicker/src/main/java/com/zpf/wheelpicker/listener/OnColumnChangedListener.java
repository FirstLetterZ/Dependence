package com.zpf.wheelpicker.listener;

import androidx.annotation.Nullable;

public interface OnColumnChangedListener<T> {
    void onColumnDataChanged(int column, @Nullable T oldValue, @Nullable T newValue);
}