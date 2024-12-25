package com.zpf.tool;

import java.util.Arrays;

public class StabilizerDouble {
    private final double[] records;
    private final double initValue;
    private double lastValue;
    private int size = 0;

    public StabilizerDouble(int size) {
        this(0f, size);
    }
    public StabilizerDouble(double initValue, int size) {
        this.initValue = initValue;
        records = new double[size];
        clear();
    }

    public double add(double value) {
        if (records.length < 2) {
            lastValue = value;
            size = 1;
            return lastValue;
        }
        size = Math.min(size + 1, records.length);
        double sum = 0.0;
        double max = value;
        double min = value;
        for (int i = size - 1; i >= 0; i--) {
            if (i == 0) {
                records[i] = value;
            } else {
                records[i] = records[i - 1];
            }
            double current = records[i];
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

    public double getValue() {
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
