package com.zpf.wheelpicker.picker;

import com.zpf.wheelpicker.interfaces.IWheelItemData;
import com.zpf.wheelpicker.model.WheelItemStyle;

public class PickerDayItem implements IWheelItemData {
    private WheelItemStyle itemStyle;
    private final int year;
    private final int month;
    private final int day;
    private String desc;

    public PickerDayItem(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        if (month < 10) {
            desc = "0" + month + "月";
        } else {
            desc = "" + month + "月";
        }
        if (day < 10) {
            desc = desc + "0" + day + "日";
        } else {
            desc = desc + "" + day + "日";
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

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }
}
