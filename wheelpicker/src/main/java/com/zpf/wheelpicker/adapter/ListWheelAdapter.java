package com.zpf.wheelpicker.adapter;

import androidx.annotation.Nullable;

import com.zpf.wheelpicker.interfaces.IPickerViewData;

import java.util.List;

public class ListWheelAdapter<T> implements WheelAdapter<T> {
    protected List<? extends T> items = null;

    public ListWheelAdapter() {
    }

    public ListWheelAdapter(@Nullable List<? extends T> items) {
        this.items = items;
    }

    @Override
    public T getItem(int index) {
        if (items != null && index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null;
    }

    public void changeDataSource(@Nullable List<? extends T> source) {
        items = source;
    }

    @Override
    public int getItemsCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    @Override
    public int indexOf(T t) {
        if (t == null || items == null || items.size() == 0) {
            return -1;
        }
        List<? extends T> list = items;
        T item;
        for (int i = 0; i < list.size(); i++) {
            item = list.get(i);
            if (t.equals(item)) {
                return i;
            }
            if (t instanceof IPickerViewData) {
                if (((IPickerViewData) t).getPickerViewText().equals(((IPickerViewData) item).getPickerViewText())) {
                    return i;
                }
            }
            if (t.toString().equals(item.toString())) {
                return i;
            }
        }
        return list.indexOf(t);
    }

}
