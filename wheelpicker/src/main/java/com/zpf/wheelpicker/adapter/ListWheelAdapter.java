package com.zpf.wheelpicker.adapter;

import java.util.List;

public class ListWheelAdapter<T> implements WheelAdapter<T> {

    private List<T> items;

    public ListWheelAdapter(List<T> items) {
        this.items = items;
    }

    @Override
    public T getItem(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null;
    }

    public void changeDataSource(List<T> source) {
        items = source;
    }

    @Override
    public int getItemsCount() {
        return items.size();
    }

    @Override
    public int indexOf(Object o) {
        return items.indexOf(o);
    }

}
