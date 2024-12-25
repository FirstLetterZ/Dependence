package com.zpf.tool;

import java.util.Arrays;

public class StabilizerLong {
    private final long[] records;
    private final long initValue;
    private long lastValue;
    private int size = 0;

    public StabilizerLong(int size) {
        this(0L, size);
    }
    public StabilizerLong(long initValue, int size) {
        this.initValue = initValue;
        records = new long[size];
        clear();
    }

    public long add(long value) {
        if (records.length < 2) {
            lastValue = value;
            size = 1;
            return lastValue;
        }
        size = Math.min(size + 1, records.length);
        long sum = 0L;
        long max = value;
        long min = value;
        for (int i = size - 1; i >= 0; i--) {
            if (i == 0) {
                records[i] = value;
            } else {
                records[i] = records[i - 1];
            }
            long current = records[i];
            sum = sum + current;
            if (current > max) {
                max = current;
            }
            if (current < min) {
                min = current;
            }
        }
        if (size < 5) {
            lastValue = sum / size;
        } else {
            sum = sum - max - min;
            lastValue = sum / (size - 2);
        }
        return lastValue;
    }

    public long getValue() {
        return lastValue;
    }

    public int size() {
        return size;
    }

    public void clear() {
        Arrays.fill(records, initValue);
        lastValue = initValue;
        size = 0;
    }
}
