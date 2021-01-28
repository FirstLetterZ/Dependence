package com.zpf.wheelpicker.picker;

import android.graphics.Color;

import com.zpf.wheelpicker.adapter.ListWheelAdapter;
import com.zpf.wheelpicker.adapter.WheelAdapter;
import com.zpf.wheelpicker.interfaces.ILinkageManager;
import com.zpf.wheelpicker.interfaces.IWheelDataModel;
import com.zpf.wheelpicker.interfaces.IWheelItemData;
import com.zpf.wheelpicker.listener.OnBoundaryChangedListener;
import com.zpf.wheelpicker.listener.OnItemSelectedListener;
import com.zpf.wheelpicker.listener.OnSelectBoundaryListener;
import com.zpf.wheelpicker.model.WheelItemStyle;
import com.zpf.wheelpicker.view.WheelView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BoundaryTimePickerDataModel implements IWheelDataModel<DateInfo> {

    private final ArrayList<OnSelectBoundaryListener> selectedListeners;
    private final ArrayList<PickerDayItem> dayList = new ArrayList<>();
    private final ArrayList<PickerTimeItem> hourList = new ArrayList<>();
    private final ArrayList<PickerTimeItem> minuteList = new ArrayList<>();
    private final ArrayList<ListWheelAdapter<? extends IWheelItemData>> adapterList = new ArrayList<>();
    private ILinkageManager linkageManager;
    private int daySize;
    private final int[] selectIndex = new int[3];
    private DateInfo selectDate;
    private long startTimestamp;
    private long endTimestamp;
    private final WheelItemStyle[] customStyles = new WheelItemStyle[3];
    private final WheelItemStyle errorStyle = new WheelItemStyle(
            Color.parseColor("#5C0000"), Color.RED);

    public BoundaryTimePickerDataModel(int daySize) {
        this.daySize = daySize;
        selectedListeners = new ArrayList<>();
        for (int i = 0; i < 3; i++)
            selectedListeners.add(new OnSelectBoundaryListener(i) {
                @Override
                protected void changeNextItemBoundary(int itemPosition, int lowerBoundary, int upperBoundary) {
                    final ILinkageManager manager = linkageManager;
                    if (manager != null) {
                        manager.changeItemBoundary(itemPosition, lowerBoundary, upperBoundary);
                    }
                }

                @Override
                protected void OnBoundaryChanged(WheelView view, int position) {
                    if (position == 0) {
                        refreshDataStyle(view, dayList);
                    } else if (position == 1) {
                        refreshDataStyle(view, hourList);
                    } else if (position == 2) {
                        refreshDataStyle(view, minuteList);
                    }
                }
            });
    }

    @Override
    public WheelAdapter<?> getAdapter(int position) {
        if (adapterList.size() == 0) {
            adapterList.add(new ListWheelAdapter<>(dayList));
            adapterList.add(new ListWheelAdapter<>(hourList));
            adapterList.add(new ListWheelAdapter<>(minuteList));
        }
        return adapterList.get(position);
    }

    @Override
    public int getListSize() {
        return 3;
    }

    @Override
    public OnItemSelectedListener getSelectedListener(int position) {
        return selectedListeners.get(position);
    }

    @Override
    public OnBoundaryChangedListener getBoundaryListener(int position) {
        return selectedListeners.get(position);
    }

    @Override
    public boolean hasBoundary() {
        return true;
    }

    @Override
    public int getSelectIndex(int position) {
        return selectIndex[position];
    }

    @Override
    public void setBoundary(DateInfo start, DateInfo end) {
        if (start == null || end == null) {
            startTimestamp = 0;
            daySize = 30;
        } else {
            if (start.getTime() < System.currentTimeMillis() + 300 * 1000) {
                startTimestamp = 0;
            } else {
                startTimestamp = start.getTime();
            }
            int days = Math.round((end.getTime() - startTimestamp) * 1.0f / (24 * 3600 * 1000)) + 1;
            if (days > 3) {
                endTimestamp = end.getTime();
                daySize = days;
            } else {
                endTimestamp = 0;
                daySize = 3;
            }
        }
    }

    @Override
    public void setInitData(DateInfo data) {
        this.selectDate = data;
    }

    @Override
    public DateInfo getSelectData() {
        PickerDayItem dayItem = dayList.get(selectedListeners.get(0).getCurrentIndex());
        PickerTimeItem hourItem = hourList.get(selectedListeners.get(1).getCurrentIndex());
        PickerTimeItem minuteItem = minuteList.get(selectedListeners.get(2).getCurrentIndex());
        return new DateInfo(dayItem.getYear(), dayItem.getMonth(),
                dayItem.getDay(), hourItem.getTime(), minuteItem.getTime());
    }

    @Override
    public void setLinkageManager(ILinkageManager manager) {
        this.linkageManager = manager;
    }

    @Override
    public void refreshDataList() {
        Calendar starTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        long lowerTime = startTimestamp;
        long upperTime = endTimestamp;
        int startMinute;
        int endMinute;
        int startHour;
        int endHour;
        if (lowerTime > System.currentTimeMillis() + 300 * 1000) {
            starTime.setTimeInMillis(startTimestamp);
            startMinute = starTime.get(Calendar.MINUTE);
        } else {
            lowerTime = System.currentTimeMillis();
            //增加5分钟，当前时间>=55时，保证时间取值在下一个区间
            starTime.set(Calendar.MINUTE, starTime.get(Calendar.MINUTE) + 5);
            startMinute = starTime.get(Calendar.MINUTE) - 5;
            if (startMinute < 0) {
                startMinute = 0;
            }
        }
        startHour = starTime.get(Calendar.HOUR_OF_DAY);
        if (upperTime - lowerTime < 72 * 3600 * 1000) {
            upperTime = lowerTime + 72 * 3600 * 1000;
        }
        endTime.setTimeInMillis(upperTime);
        endMinute = endTime.get(Calendar.MINUTE);
        endHour = endTime.get(Calendar.HOUR_OF_DAY);

        minuteList.clear();
        int startMinuteIndex = -1;
        int endMinuteIndex = -1;
        for (int i = 0; i < 60; i = i + 5) {
            PickerTimeItem itemInfo = new PickerTimeItem(i);
            if (startMinuteIndex < 0 && i >= startMinute) {
                startMinuteIndex = i / 5;
            }
            if (endMinuteIndex < 0 && i >= endMinute) {
                endMinuteIndex = i / 5;
            }
            minuteList.add(itemInfo);
        }
        if (startMinuteIndex < 0) {
            startMinuteIndex = 0;
        }
        if (endMinuteIndex < 0) {
            endMinuteIndex = 0;
        }

        hourList.clear();
        for (int i = 0; i < 24; i++) {
            PickerTimeItem itemInfo = new PickerTimeItem(i);
            hourList.add(itemInfo);
        }

        dayList.clear();
        int day;
        PickerDayItem dayItem;
        while (dayList.size() < daySize) {
            day = starTime.get(Calendar.DAY_OF_MONTH);
            dayItem = new PickerDayItem(starTime.get(Calendar.YEAR),
                    starTime.get(Calendar.MONTH) + 1, day);
            dayList.add(dayItem);
            day = day + 1;
            starTime.set(Calendar.DAY_OF_MONTH, day);
        }
        selectIndex[0] = 0;
        selectIndex[1] = startHour;
        selectIndex[2] = startMinuteIndex;
        if (selectDate != null && selectDate.getTime() > lowerTime && selectDate.getTime() < upperTime) {
            for (int i = 0; i < dayList.size(); i++) {
                if (dayList.get(i).getYear() == selectDate.year && dayList.get(i).getMonth() == selectDate.month
                        && dayList.get(i).getDay() == selectDate.day) {
                    selectIndex[0] = i;
                    break;
                }
            }
            selectIndex[1] = selectDate.hour;
            for (int i = 0; i < minuteList.size(); i++) {
                if (minuteList.get(i).getTime() >= selectDate.minute) {
                    selectIndex[2] = i;
                    break;
                }
            }
        }
        selectedListeners.get(0).resetState();
        selectedListeners.get(1).resetState();
        selectedListeners.get(2).resetState();
        selectedListeners.get(0).setNextItemBoundary(startHour, Integer.MAX_VALUE,
                -1, endHour, -1, Integer.MAX_VALUE);
        selectedListeners.get(1).setNextItemBoundary(startMinuteIndex, Integer.MAX_VALUE,
                -1, endMinuteIndex, -1, Integer.MAX_VALUE);
    }

    public void setCustomStyles(WheelItemStyle outLowerStyles, WheelItemStyle inMiddleStyles, WheelItemStyle outUpperStyles) {
        this.customStyles[0] = outLowerStyles;
        this.customStyles[1] = inMiddleStyles;
        this.customStyles[2] = outUpperStyles;
    }

    protected void refreshDataStyle(WheelView wheelView, List<? extends IWheelItemData> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        WheelItemStyle itemStyle;
        for (int i = 0; i < list.size(); i++) {
            if (i < wheelView.getLowerBoundary()) {
                itemStyle = customStyles[0];
                if (itemStyle == null) {
                    itemStyle = errorStyle;
                }
            } else if (i > wheelView.getUpperBoundary()) {
                itemStyle = customStyles[2];
                if (itemStyle == null) {
                    itemStyle = errorStyle;
                }
            } else {
                itemStyle = customStyles[1];
            }
            list.get(i).setItemStyle(itemStyle);
        }
        wheelView.invalidate();
    }
}