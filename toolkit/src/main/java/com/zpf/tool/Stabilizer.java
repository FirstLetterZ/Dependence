package com.zpf.tool;

import androidx.annotation.Nullable;

import java.util.Arrays;

public class Stabilizer {
    private final Number[] records;
    private Number lastValue;

    public Stabilizer(int size) {
        this(0.0, size);
    }
    public Stabilizer(@Nullable Number initValue, int size) {
        records = new Number[size];
        lastValue = initValue;
        if (initValue != null && initValue.doubleValue() != 0.0) {
            Arrays.fill(records, initValue);
        }
    }

    public Number add(Number value) {
        int size = records.length;
        if (size < 2) {
            lastValue = value;
            return lastValue;
        }
        double sum = 0.0;
        if (size < 5) {
            for (int i = 0; i < size; i++) {
                if (i == records.length - 1) {
                    records[i] = value;
                } else {
                    records[i] = records[i + 1];
                }
                sum = sum + records[i].doubleValue();
            }
            lastValue = sum / size;
        } else {
            double max = value.doubleValue();
            double min = value.doubleValue();
            for (int i = 0; i < size; i++) {
                if (i == records.length - 1) {
                    records[i] = value;
                } else {
                    records[i] = records[i + 1];
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
            sum = sum - max - min;
            lastValue = sum / (size - 2);
        }
        return lastValue;
    }

    public Number getValue() {
        return lastValue;
    }
}
