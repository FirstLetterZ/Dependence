package com.zpf.wheelpicker.picker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.wheelpicker.interfaces.IDataSource;
import com.zpf.wheelpicker.model.WheelColumnHolder;

import java.util.List;

public class LinkagePickerModel<T> extends AbsPickerModel<T, List<T>> {
    protected final IDataSource<T> dataSource;

    public LinkagePickerModel(int size, IDataSource<T> dataSource) {
        resetSize(size);
        this.dataSource = dataSource;
        linkage = true;
    }

    public List<T> getSelectData() {
        return getSelectList();
    }

    @Override
    protected void updateColumnData(int column, @NonNull WheelColumnHolder<T> holder, @Nullable WheelColumnHolder<T> lastHolder) {
        holder.changeDataSource(dataSource.getColumnList(getSelectData(), column));
    }

}
