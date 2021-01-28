package com.zpf.wheelpicker.interfaces;

import com.zpf.wheelpicker.model.WheelItemStyle;

public interface IWheelItemData extends IPickerViewData {
    WheelItemStyle getItemStyle();

    void setItemStyle(WheelItemStyle itemStyle);
}