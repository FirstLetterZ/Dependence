package com.zpf.wheelpicker.interfaces;

import java.util.List;

public interface ILinkageViewData<T extends ILinkageViewData<T>> extends IPickerViewData {
    List<T> getNext();
}
