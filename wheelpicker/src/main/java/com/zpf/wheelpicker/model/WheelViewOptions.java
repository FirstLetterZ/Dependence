package com.zpf.wheelpicker.model;

import android.content.res.TypedArray;
import android.view.Gravity;

import com.zpf.wheelpicker.R;
import com.zpf.wheelpicker.view.WheelView;

public class WheelViewOptions {
    public int dividerColor;
    public int dividerWidth;
    public WheelView.DividerType dividerType = WheelView.DividerType.FILL;//分隔线类型
    public int itemGravity = Gravity.CENTER;
    public final WheelItemStyle itemStyle = new WheelItemStyle();//条目样式
    private int itemsVisibleCount = 11;
    public int textXOffset = 0;
    private float lineSpacingMultiplier = 1.6F;
    public boolean isAlphaGradient = false; //透明度渐变
    public boolean isLoop;
    public boolean isOptions = false;
    public int labelGravity = Gravity.LEFT;
    public String label;//附加单位
    public boolean isCenterLabel = true;

    public void initWithOptions(WheelViewOptions options) {
        if (options != null) {
            dividerColor = options.dividerColor;
            dividerWidth = options.dividerWidth;
            dividerType = options.dividerType;
            itemGravity = options.itemGravity;
            isLoop = options.isLoop;
            textXOffset = options.textXOffset;
            isAlphaGradient = options.isAlphaGradient;
            isOptions = options.isOptions;
            itemStyle.textColorOut = options.itemStyle.textColorOut;
            itemStyle.textColorCenter = options.itemStyle.textColorCenter;
            itemStyle.textSize = options.itemStyle.textSize;
            setLineSpacingMultiplier(options.lineSpacingMultiplier);
            labelGravity =options.labelGravity ;
            label =options.label ;
            setItemsVisibleCount(options.itemsVisibleCount);
            isCenterLabel = options.isCenterLabel;
        }
    }

    public void initWithTypedArray(TypedArray a) {
        if (a != null) {
            itemGravity = a.getInt(R.styleable.WheelView_itemGravity, Gravity.CENTER);
            isLoop = a.getBoolean(R.styleable.WheelView_loop, false);
            itemStyle.textColorOut = a.getColor(R.styleable.WheelView_textColorOut, 0xFFa8a8a8);
            itemStyle.textColorCenter = a.getColor(R.styleable.WheelView_textColorCenter, 0xFF2a2a2a);
            dividerColor = a.getColor(R.styleable.WheelView_dividerColor, 0xFFd5d5d5);
            dividerWidth = a.getDimensionPixelSize(R.styleable.WheelView_dividerWidth, 2);
            float textSize = a.getDimension(R.styleable.WheelView_textSize, -1);
            if (textSize > 0) {
                itemStyle.textSize = textSize;
            }
            setLineSpacingMultiplier(a.getFloat(R.styleable.WheelView_lineSpacingMultiplier, 1.6f));
            labelGravity = a.getInt(R.styleable.WheelView_labelGravity, Gravity.CENTER);
            label = a.getString(R.styleable.WheelView_label);
            setItemsVisibleCount(a.getInt(R.styleable.WheelView_visibleCount, 11));
            isCenterLabel = a.getBoolean(R.styleable.WheelView_centerLabel, true);
        }
    }

    public int getItemsVisibleCount() {
        return itemsVisibleCount;
    }

    public void setItemsVisibleCount(int itemsVisibleCount) {
        if (itemsVisibleCount % 2 == 0) {
            itemsVisibleCount += 1;
        }
        this.itemsVisibleCount = itemsVisibleCount + 2; //第一条和最后一条
    }

    public float getLineSpacingMultiplier() {
        return lineSpacingMultiplier;
    }

    public void setLineSpacingMultiplier(float newSpacingMultiplier) {
        if (newSpacingMultiplier < 1.0f) {
            lineSpacingMultiplier = 1.0f;
        } else if (newSpacingMultiplier > 4.0f) {
            lineSpacingMultiplier = 4.0f;
        } else {
            lineSpacingMultiplier = newSpacingMultiplier;
        }
    }
}