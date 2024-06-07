package com.zpf.wheelpicker.picker;

import androidx.annotation.NonNull;

import com.zpf.wheelpicker.interfaces.ILinkageViewData;

import java.util.List;

public class LinkagePickerModel2<T extends ILinkageViewData<T>> extends LinkagePickerModel<T> {

    public LinkagePickerModel2(int size, @NonNull final List<T> firstColumn) {
        super(size, (selects, column) -> {
            if (column == 0) {
                return firstColumn;
            }
            if (selects == null || selects.size() < column) {
                return null;
            }
            T selectVal = selects.get(column - 1);
            if (selectVal == null) {
                return null;
            }
            return selectVal.getNext();
        });
    }

}