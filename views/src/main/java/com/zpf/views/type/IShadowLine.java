package com.zpf.views.type;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import androidx.annotation.ColorInt;

/**
 * Created by ZPF on 2019/3/28.
 */
public interface IShadowLine {

    void setElevation(int elevation);

    void setShadowColor(@ColorInt int startColor);

    void setShadowOrientation(GradientDrawable.Orientation orientation);

    void setShadowDrawable(Drawable background);

    View getView();

}
