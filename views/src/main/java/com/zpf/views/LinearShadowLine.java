package com.zpf.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.zpf.views.type.IShadowLine;

/**
 * Created by ZPF on 2019/3/28.
 */
public class LinearShadowLine extends View implements IShadowLine {
    private int elevation;
    private int shadowColor = 0;
    private GradientDrawable.Orientation orientation = GradientDrawable.Orientation.TOP_BOTTOM;

    public LinearShadowLine(Context context) {
        super(context);
    }

    public LinearShadowLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearShadowLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setElevation(int elevation) {
        if (elevation > 0 && this.elevation != elevation) {
            this.elevation = elevation;
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            if (layoutParams != null) {
                layoutParams.height = (int) (elevation * getResources().getDisplayMetrics().density);
            } else {
                layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        (int) (elevation * getResources().getDisplayMetrics().density));
            }
            setLayoutParams(layoutParams);
        }
    }

    @Override
    public void setShadowColor(@ColorInt int startColor) {
        shadowColor = startColor;
        GradientDrawable gradientDrawable = new GradientDrawable(orientation,
                new int[]{startColor, Color.TRANSPARENT});
        setBackground(gradientDrawable);
    }

    @Override
    public void setShadowOrientation(GradientDrawable.Orientation orientation) {
        if (this.orientation != orientation) {
            this.orientation = orientation;
            if (shadowColor != 0) {
                setShadowColor(shadowColor);
            }
        }
    }

    @Override
    public void setShadowDrawable(Drawable background) {
        shadowColor = 0;
        setBackground(background);
    }

    @Override
    public View getView() {
        return this;
    }

}
