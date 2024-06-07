package com.zpf.tool;

import java.util.Arrays;

public class StabilizerInt {
    private final int[] records;
    private int lastValue;
    public StabilizerInt(int size) {
        this(0, size);
    }
    public StabilizerInt(int initValue, int size) {
        records = new int[size];
        lastValue = initValue;
        if (initValue != 0) {
            Arrays.fill(records, initValue);
        }
    }

    public int add(int value) {
        int size = records.length;
        if (size < 2) {
            lastValue = value;
            return lastValue;
        }
        int sum = 0;
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
            int max = value;
            int min = value;
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

    public int getValue() {
        return lastValue;
    }
}
