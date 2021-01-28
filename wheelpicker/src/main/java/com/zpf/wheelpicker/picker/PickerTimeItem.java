package com.zpf.wheelpicker.picker;

import com.zpf.wheelpicker.interfaces.IWheelItemData;
import com.zpf.wheelpicker.model.WheelItemStyle;

public class PickerTimeItem implements IWheelItemData {
    private WheelItemStyle itemStyle ;
    private final int time;
    private final String desc;

    public PickerTimeItem(int time) {
        this.time = time;
        if (time < 10) {
            desc = "0" + time;
        } else {
            desc = "" + time;
        }
    }

    @Override
    public void setItemStyle(WheelItemStyle itemStyle) {
        this.itemStyle = itemStyle;
    }

    @Override
    public WheelItemStyle getItemStyle() {
        return itemStyle;
    }

    @Override
    public String getPickerViewText() {
        return desc;
    }

    public int getTime() {
        return time;
    }
}
