package com.zpf.wheelpicker.interfaces;

import androidx.annotation.Nullable;

import java.util.List;

public interface IDataSource<T> {
    List<? extends T> getColumnList(@Nullable List<T> selects, int column);
}