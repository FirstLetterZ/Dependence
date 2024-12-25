package com.zpf.tool;

import java.util.Arrays;

public class StabilizerInt {
    private final int[] records;
    private final int initValue;
    private int lastValue;
    private int size = 0;

    public StabilizerInt(int size) {
        this(0, size);
    }
    public StabilizerInt(int initValue, int size) {
        this.initValue = initValue;
        records = new int[size];
        lastValue = initValue;
        if (initValue != 0) {
            Arrays.fill(records, initValue);
        }
    }

    public int add(int value) {
        if (records.length < 2) {
            lastValue = value;
            size = 1;
            return lastValue;
        }
        size = Math.min(size + 1, records.length);
        int sum = 0;
        int max = value;
        int min = value;
        for (int i = size - 1; i >= 0; i--) {
            if (i == 0) {
                records[i] = value;
            } else {
                records[i] = records[i - 1];
            }
            int current = records[i];
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

    public int getValue() {
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
