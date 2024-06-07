package com.zpf.tool;

import java.util.Arrays;

public class StabilizerLong {
    private final long[] records;
    private long lastValue;
    public StabilizerLong(int size) {
        this(0L, size);
    }
    public StabilizerLong(long initValue, int size) {
        records = new long[size];
        lastValue = initValue;
        if (initValue != 0L) {
            Arrays.fill(records, initValue);
        }
    }

    public long add(long value) {
        long size = records.length;
        if (size < 2) {
            lastValue = value;
            return lastValue;
        }
        long sum = 0L;
        if (size < 5) {
            for (int i = 0; i < size; i++) {
                if (i == records.length - 1) {
                    records[i] = value;
                } else {
                    records[i] = records[i + 1];
                }
                sum = sum + records[i];
            }
            lastValue = sum / size;
        } else {
            long max = value;
            long min = value;
            for (int i = 0; i < size; i++) {
                if (i == records.length - 1) {
                    records[i] = value;
                } else {
                    records[i] = records[i + 1];
                }
                sum = sum + records[i];
                if (records[i] > max) {
                    max = records[i];
                }
                if (records[i] < min) {
                    min = records[i];
                }
            }
            sum = sum - max - min;
            lastValue = sum / (size - 2);
        }
        return lastValue;
    }

    public long getValue() {
        return lastValue;
    }
}
