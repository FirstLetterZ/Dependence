package com.zpf.tool;

import java.util.Arrays;

public class StabilizerFloat {
    private final float[] records;
    private final float initValue;
    private float lastValue;
    private int size = 0;

    public StabilizerFloat(int size) {
        this(0f, size);
    }
    public StabilizerFloat(float initValue, int size) {
        this.initValue = initValue;
        records = new float[size];
        clear();
    }

    public float add(float value) {
        if (records.length < 2) {
            lastValue = value;
            size = 1;
            return lastValue;
        }
        size = Math.min(size + 1, records.length);
        float sum = 0f;
        float max = value;
        float min = value;
        for (int i = size - 1; i >= 0; i--) {
            if (i == 0) {
                records[i] = value;
            } else {
                records[i] = records[i - 1];
            }
            float current = records[i];
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

    public float getValue() {
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
