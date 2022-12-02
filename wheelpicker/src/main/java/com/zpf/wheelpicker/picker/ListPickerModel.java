package com.zpf.wheelpicker.picker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.wheelpicker.model.WheelColumnHolder;

import java.util.List;

public class ListPickerModel<T> extends AbsPickerModel<T, List<T>> {
    private final List<List<? extends T>> dataSource;

    public ListPickerModel(@NonNull List<List<? extends T>> dataSource) {
        resetSize(dataSource.size());
        this.dataSource = dataSource;
    }

    @Nullable
    @Override
    public List<T> getSelectData() {
        return getSelectList();
    }

    @Override
    protected void updateColumnData(int column, @NonNull WheelColumnHolder<T> holder, @Nullable WheelColumnHolder<T> lastHolder) {
        if (column < dataSource.size()) {
            holder.changeDataSource(dataSource.get(column));
        } else {
            holder.changeDataSource(null);
        }
    }
}