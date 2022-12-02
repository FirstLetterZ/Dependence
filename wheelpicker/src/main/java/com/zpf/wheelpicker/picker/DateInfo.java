package com.zpf.wheelpicker.picker;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;

public class DateInfo implements Comparable<DateInfo>, Parcelable {
    public final int year;
    public final int month;
    public final int day;
    public final int hour;
    public final int minute;
    public final long time;

    public DateInfo() {
        this(Calendar.getInstance());
    }

    public DateInfo(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH) + 1;
        this.day = calendar.get(Calendar.DATE);
        this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        this.minute = calendar.get(Calendar.MINUTE);
        this.time = calendar.getTimeInMillis();
    }

    public DateInfo(Calendar calendar) {
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH) + 1;
        this.day = calendar.get(Calendar.DATE);
        this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        this.minute = calendar.get(Calendar.MINUTE);
        this.time = calendar.getTimeInMillis();
    }

    public DateInfo(int[] dateNumber) {
        int[] array = new int[5];
        Calendar calendar = Calendar.getInstance();
        if (dateNumber != null && dateNumber.length > 0) {
            int dateValue;
            for (int i = 0; i < 5; i++) {
                dateValue = -1;
                if (i < dateNumber.length) {
                    dateValue = dateNumber[i];
                }
                if (dateValue < 0) {
                    if (i < 3) {
                        array[i] = 1;
                    } else {
                        array[i] = 0;
                    }
                }
            }
            calendar.set(Calendar.YEAR, array[0]);
            calendar.set(Calendar.MONTH, array[1] - 1);
            calendar.set(Calendar.DATE, array[2]);
            calendar.set(Calendar.HOUR_OF_DAY, array[3]);
            calendar.set(Calendar.MINUTE, array[4]);
        }
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH) + 1;
        this.day = calendar.get(Calendar.DATE);
        this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        this.minute = calendar.get(Calendar.MINUTE);
        this.time = calendar.getTimeInMillis();
    }

    public DateInfo(int year, int month, int day) {
        this(year, month, day, 0, 0);
    }

    public DateInfo(int year, int month, int day, int hour, int minute) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        if (year <= 0 || month <= 0 || day <= 0 || hour < 0 || minute < 0) {
            this.time = Integer.MIN_VALUE;
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DATE, day);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            this.time = calendar.getTimeInMillis();
        }
    }

    protected DateInfo(Parcel in) {
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
        hour = in.readInt();
        minute = in.readInt();
        time = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(year);
        dest.writeInt(month);
        dest.writeInt(day);
        dest.writeInt(hour);
        dest.writeInt(minute);
        dest.writeLong(time);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DateInfo> CREATOR = new Creator<DateInfo>() {
        @Override
        public DateInfo createFromParcel(Parcel in) {
            return new DateInfo(in);
        }

        @Override
        public DateInfo[] newArray(int size) {
            return new DateInfo[size];
        }
    };

    public int get(int position) {
        switch (position) {
            case 0:
                return year;
            case 1:
                return month;
            case 2:
                return day;
            case 3:
                return hour;
            case 4:
                return minute;
        }
        return -1;
    }

    public int[] toArray() {
        return new int[]{year, month, day, hour, minute};
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
