package com.zpf.wheelpicker.adapter;

public class EmptyWheelAdapter implements WheelAdapter<String> {

    @Override
    public int getItemsCount() {
        return 1;
    }

    @Override
    public String getItem(int index) {
        return "";
    }

    @Override
    public int indexOf(String o) {
        return 0;
    }
}
