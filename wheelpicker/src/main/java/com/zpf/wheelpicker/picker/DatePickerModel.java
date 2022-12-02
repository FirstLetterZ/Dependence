package com.zpf.wheelpicker.picker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.wheelpicker.interfaces.ILinkageViewManager;
import com.zpf.wheelpicker.model.WheelColumnHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatePickerModel extends AbsPickerModel<Integer, DateInfo> {
    private final boolean[] showColumn;
    private final int[] columnPosition = new int[5];
    private final DateInfo defaultSelect = new DateInfo(Calendar.getInstance());
    private final List<List<Integer>> columnList = new ArrayList<>();
    private final int showSize;

    public DatePickerModel(boolean year, boolean month, boolean day, boolean hour, boolean minute) {
        this.showColumn = new boolean[]{year, month, day, hour, minute};
        int count = 0;
        for (int i = 0; i < showColumn.length; i++) {
            if (showColumn[i]) {
                columnPosition[count] = i;
                count++;
            } else {
                columnPosition[i] = -1;
            }
            columnList.add(new ArrayList<>());
        }
        showSize = count;
        resetSize(showColumn.length);
    }

    @Override
    public int getSize() {
        return showSize;
    }

    @Override
    protected int getRealPosition(int position) {
        if (position >= 0 && position < columnPosition.length) {
            return columnPosition[position];
        } else {
            return super.getRealPosition(position);
        }
    }

    @Override
    protected void updateColumnData(int column, @NonNull WheelColumnHolder<Integer> holder, @Nullable WheelColumnHolder<Integer> lastHolder) {
        List<Integer> list = columnList.get(column);
        if (column == 0) {
            initYearArray(list, parseValue(holder.selectData, defaultSelect.get(0)));
        } else if (column == 1) {
            fillList(list, 1, 12);
        } else if (column == 2) {
            int year = parseValue(holderList.get(0).selectData, defaultSelect.get(0));
            int month = parseValue(holderList.get(1).selectData, defaultSelect.get(1));
            fillList(list, 1, getDayInMonth(year, month));
        } else if (column == 3) {
            fillList(list, 0, 23);
        } else if (column == 4) {
            fillList(list, 0, 59);
        }
        holder.changeDataSource(list);
    }

    @Nullable
    @Override
    protected Integer getColumnInitValue(int column) {
        if (initSelectInfo == null) {
            return null;
        }
        return initSelectInfo.get(column);
    }

    @Nullable
    @Override
    protected Integer getColumnBoundaryValue(int column, DateInfo boundary) {
        return boundary.get(column);
    }


    @Override
    protected ILinkageViewManager getLinkageViewManager(int column) {
        if (showColumn[column]) {
            return super.getLinkageViewManager(column);
        }
        return null;
    }

    @Override
    @Nullable
    public DateInfo getSelectData() {
        int[] dates = new int[holderList.size()];
        for (int i = 0; i < holderList.size(); i++) {
            Integer itemSelect = holderList.get(i).selectData;
            if (itemSelect == null) {
                return null;
            } else {
                dates[i] = itemSelect;
            }
        }
        return new DateInfo(dates);
    }

    private int parseValue(Integer value, int defValue) {
        if (value != null && value > 0) {
            return value;
        } else {
            return defValue;
        }
    }

    private void initYearArray(@NonNull List<Integer> list, int currentYear) {
        int startLimit = parseValue(getColumnBoundaryValue(0, boundaryLower), -1);
        int endLimit = parseValue(getColumnBoundaryValue(0, boundaryUpper), -1);
        if (startLimit < 0) {
            startLimit = Integer.MIN_VALUE;
        }
        if (endLimit < 0) {
            endLimit = Integer.MAX_VALUE;
        }
        if (startLimit > endLimit) {
            int tempLimit = startLimit;
            startLimit = endLimit;
            endLimit = tempLimit;
        }
        int start;
        int end;
        if (startLimit < 0 && endLimit == Integer.MAX_VALUE) {
            start = currentYear - 50;
            end = currentYear + 50;
        } else if (startLimit < 0) {
            start = endLimit - 100;
            end = endLimit;
        } else if (endLimit == Integer.MAX_VALUE) {
            start = startLimit;
            end = startLimit + 100;
        } else {
            start = startLimit;
            end = endLimit;
        }
        list.clear();
        for (int n = start; n <= end; n++) {
            list.add(n);
        }
    }

    private int getDayInMonth(int currentYear, int currentMonth) {
        int days;
        if (currentMonth == 2) {
            if (currentYear % 4 == 0) {
                days = 29;
            } else {
                days = 28;
            }
        } else if (currentMonth == 4 || currentMonth == 6 || currentMonth == 9 || currentMonth == 11) {
            days = 30;
        } else {
            days = 31;
        }
        return days;
    }

    private void fillList(@NonNull List<Integer> list, int start, int end) {
        list.clear();
        for (int n = start; n <= end; n++) {
            list.add(n);
        }
    }

}