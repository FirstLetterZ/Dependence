package com.zpf.tool;

import androidx.annotation.Nullable;

import java.util.Arrays;

public class Stabilizer {
    private final Number[] records;
    private final Number initValue;
    private Number lastValue;
    private int size = 0;

    public Stabilizer(int size) {
        this(0.0, size);
    }
    public Stabilizer(@Nullable Number initValue, int size) {
        this.initValue = initValue;
        records = new Number[size];
        clear();
    }

    public Number add(Number value) {
        if (records.length < 2) {
            lastValue = value;
            size = 1;
            return lastValue;
        }
        size = Math.min(size + 1, records.length);
        double sum = 0.0;
        double max = value.doubleValue();
        double min = value.doubleValue();
        for (int i = size - 1; i >= 0; i--) {
            if (i == 0) {
                records[i] = value;
            } else {
                records[i] = records[i - 1];
            }
            double current = records[i].doubleValue();
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

    public Number getValue() {
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
