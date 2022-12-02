package com.zpf.wheelpicker.model;

import android.graphics.Color;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WheelItemStyle {
    public Typeface typeface = Typeface.MONOSPACE;//字体样式
    public int textColorOut = 0;//字体颜色
    public int textColorCenter = 0;//字体颜色
    public float textSize = 0;//字体大小
    public static final WheelItemStyle defErrorStyle = new WheelItemStyle(Color.RED, Color.RED);

    public WheelItemStyle() {
    }

    public WheelItemStyle(int textColorOut, int textColorCenter) {
        this.textColorOut = textColorOut;
        this.textColorCenter = textColorCenter;
    }

    public WheelItemStyle(int textColorOut, int textColorCenter, float textSize) {
        this.textColorOut = textColorOut;
        this.textColorCenter = textColorCenter;
        this.textSize = textSize;
    }

    @NonNull
    public static WheelItemStyle mergeStyle(@Nullable WheelItemStyle style, @Nullable WheelItemStyle defStyle) {
        if (defStyle == null) {
            defStyle = new WheelItemStyle();
        }
        if (style == null) {
            return defStyle;
        }

        if (style.textColorOut == 0) {
            style.textColorOut = defStyle.textColorOut;
        }
        if (style.textColorCenter == 0) {
            style.textColorCenter = defStyle.textColorCenter;
        }
        if (style.textSize <= 0) {
            style.textSize = defStyle.textSize;
        }
        if (style.typeface == null) {
            style.typeface = defStyle.typeface;
        }
        return style;
    }

}
