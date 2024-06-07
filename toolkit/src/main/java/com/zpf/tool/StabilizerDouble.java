package com.zpf.tool;

import java.util.Arrays;

public class StabilizerDouble {
    private final double[] records;
    private double lastValue;
    public StabilizerDouble(int size) {
        this(0f, size);
    }
    public StabilizerDouble(double initValue, int size) {
        records = new double[size];
        lastValue = initValue;
        if (initValue != 0.0) {
            Arrays.fill(records, initValue);
        }
    }

    public double add(double value) {
        int size = records.length;
        if (size < 2) {
            lastValue = value;
            return lastValue;
        }
        double sum = 0f;
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
            double max = value;
            double min = value;
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

    public double getValue() {
        return lastValue;
    }
}
