package com.zpf.wheelpicker.picker;

import com.zpf.wheelpicker.adapter.ListWheelAdapter;
import com.zpf.wheelpicker.adapter.WheelAdapter;
import com.zpf.wheelpicker.interfaces.ILinkageManager;
import com.zpf.wheelpicker.interfaces.IWheelDataModel;
import com.zpf.wheelpicker.listener.OnBoundaryChangedListener;
import com.zpf.wheelpicker.listener.OnItemSelectedListener;
import com.zpf.wheelpicker.view.WheelView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatePickerDateModel implements IWheelDataModel<Date> {
    private final ArrayList<OnItemSelectedListener> selectedListeners;
    private final ArrayList<ArrayList<Integer>> dataList = new ArrayList<>();
    private final int[] selectIndex = new int[5];
    private Date startDate;
    private Date endDate;
    private Date currentDate;
    private ILinkageManager linkageManager;
    private final ArrayList<ListWheelAdapter<Integer>> adapterList = new ArrayList<>();

    public DatePickerDateModel() {
        selectedListeners = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final int position = i;
            selectedListeners.add(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(WheelView view, int itemIndex) {
                    selectIndex[position] = itemIndex;
                    if (position == 0 || position == 1) {
                        if (initDayArray(dataList.get(0).get(selectIndex[0]),
                                dataList.get(1).get(selectIndex[1]))) {
                            if (linkageManager != null) {
                                linkageManager.notifyItemDataChanged(2, -1);
                            }
                        }
                    }
                }
            });
            ArrayList<Integer> list = new ArrayList<>();
            if (i == 1) {
                for (int j = 0; j < 12; j++) {
                    list.add(j + 1);
                }
            } else if (i == 3) {
                for (int j = 0; j < 24; j++) {
                    list.add(j);
                }
            } else if (i == 4) {
                for (int j = 0; j < 60; j++) {
                    list.add(j);
                }
            }
            dataList.add(list);
        }
    }

    @Override
    public WheelAdapter<?> getAdapter(int position) {
        if (adapterList.size() == 0) {
            for(List<Integer> list:dataList){
                adapterList.add(new ListWheelAdapter<>(list));
            }
        }
        return adapterList.get(position);
    }


    @Override
    public int getListSize() {
        return dataList.size();
    }

    @Override
    public OnItemSelectedListener getSelectedListener(int position) {
        if (position < selectedListeners.size()) {
            return selectedListeners.get(position);
        }
        return null;
    }

    @Override
    public OnBoundaryChangedListener getBoundaryListener(int position) {
        return null;
    }

    @Override
    public boolean hasBoundary() {
        return false;
    }

    @Override
    public int getSelectIndex(int position) {
        if (position < selectIndex.length) {
            return selectIndex[position];
        }
        return 0;
    }

    public void setBoundary(Date start, Date end) {
        startDate = start;
        endDate = end;
    }

    @Override
    public void setInitData(Date data) {
        currentDate = data;
    }

    @Override
    public Date getSelectData() {
        int[] result = new int[5];
        for (int i = 0; i < 5; i++) {
            result[i] = dataList.get(i).get(getSelectIndex(i));
        }
        return new Date(result[0] - 1900, result[1] - 1,
                result[2], result[3], result[4]);
    }

    @Override
    public void refreshDataList() {
        Calendar calendar = Calendar.getInstance();
        int currentYear;
        int currentMonth;
        int currentDay;
        if (currentDate != null) {
            currentYear = currentDate.getYear() + 1900;
            currentMonth = currentDate.getMonth() + 1;
            currentDay = currentDate.getDate();
        } else {
            currentYear = calendar.get(Calendar.YEAR);
            currentMonth = calendar.get(Calendar.MONTH) + 1;
            currentDay = calendar.get(Calendar.DAY_OF_MONTH);
            currentDate = calendar.getTime();
        }
        if (startDate == null) {
            calendar.set(currentYear - 100, currentMonth, currentDay);
            startDate = calendar.getTime();
        }
        if (endDate == null) {
            calendar.set(currentYear + 100, currentMonth, currentDay);
            endDate = calendar.getTime();
        }
        if (startDate.getTime() > endDate.getTime()) {
            Date temp = endDate;
            endDate = startDate;
            startDate = temp;
        }
        if (currentDate.getTime() < startDate.getTime()) {
            currentDate = startDate;
        }
        if (currentDate.getTime() > endDate.getTime()) {
            currentDate = endDate;
        }
        currentYear = currentDate.getYear() + 1900;
        currentMonth = currentDate.getMonth() + 1;
        currentDay = currentDate.getDate();
        int currentHour = currentDate.getHours();
        int currentMinute = currentDate.getMinutes();
        selectIndex[0] = currentYear - startDate.getYear() - 1900;
        selectIndex[1] = currentMonth - 1;
        selectIndex[2] = currentDay;
        selectIndex[3] = currentHour;
        selectIndex[4] = currentMinute;
        initYearArray(startDate.getYear() + 1900, endDate.getYear() + 1900);
        initDayArray(currentYear, currentMonth);
    }

    @Override
    public void setLinkageManager(ILinkageManager manager) {
        linkageManager = manager;
    }

    private void initYearArray(int from, int end) {
        ArrayList<Integer> list = dataList.get(0);
        if (list == null) {
            return;
        }
        list.clear();
        for (int j = from; j <= end; j++) {
            list.add(j);
        }
    }

    private boolean initDayArray(int currentYear, int currentMonth) {
        ArrayList<Integer> list = dataList.get(2);
        if (list == null) {
            return false;
        }
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
        if (list.size() != days) {
            list.clear();
            for (int j = 0; j < days; j++) {
                list.add(j + 1);
            }
            return true;
        }
        return false;
    }

}
