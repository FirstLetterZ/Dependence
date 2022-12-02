package com.zpf.aaa;

import com.zpf.wheelpicker.interfaces.IStyledViewData;
import com.zpf.wheelpicker.model.WheelItemStyle;

public class NumberInfo implements IStyledViewData {
    public String value;
    private WheelItemStyle itemStyle;

    public NumberInfo(int value) {
        this.value = String.valueOf(value);
    }

    @Override
    public String getPickerViewText() {
        return value;
    }

    @Override
    public void setItemStyle(WheelItemStyle itemStyle) {
        this.itemStyle = itemStyle;
    }

    @Override
    public WheelItemStyle getItemStyle() {
        return itemStyle;
    }
}
