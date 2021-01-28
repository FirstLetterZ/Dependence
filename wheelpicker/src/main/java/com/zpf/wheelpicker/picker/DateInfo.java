package com.zpf.wheelpicker.picker;

import java.util.Calendar;
import java.util.Date;

public class DateInfo implements Comparable<DateInfo> {
    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;

    public DateInfo() {
        Calendar calendar = Calendar.getInstance();
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH) + 1;
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        this.minute = calendar.get(Calendar.MINUTE);
    }

    public DateInfo(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public DateInfo(int year, int month, int day, int hour, int minute) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public long getTime() {
        return new Date(year - 1900, month - 1, day, hour, minute).getTime();
    }

    @Override
    public int compareTo(DateInfo info) {
        if (info == null) {
            return 1;
        }
        if (year > info.year) {
            return 1;
        }
        if (year < info.year) {
            return -1;
        }
        if (month > info.month) {
            return 1;
        }
        if (month < info.month) {
            return -1;
        }

        if (day > info.day) {
            return 1;
        }
        if (day < info.day) {
            return -1;
        }
        if (hour > info.hour) {
            return 1;
        }
        if (hour < info.hour) {
            return -1;
        }
        if (minute > info.minute) {
            return 1;
        }
        if (minute < info.minute) {
            return -1;
        }
        return 0;
    }

}
