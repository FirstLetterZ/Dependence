package com.zpf.aaa;

import com.zpf.wheelpicker.interfaces.IStyledViewData;
import com.zpf.wheelpicker.model.WheelItemStyle;

public class AddressBean implements IStyledViewData {
    public String label;
    public String value;
    public boolean status;
    private WheelItemStyle itemStyle ;

    public AddressBean() {
    }

    public AddressBean(String label) {
        this.label = label;
        this.value = label;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isStatus() {
        return this.status;
    }

    public void setStatus(boolean status) {
        this.status = status;
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
