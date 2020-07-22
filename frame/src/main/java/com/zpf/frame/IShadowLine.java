package com.zpf.frame;

import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import android.view.View;

/**
 * Created by ZPF on 2019/3/28.
 */
public interface IShadowLine {

    void setElevation(int elevation);

    void setShadowColor(@ColorInt int startColor);

    void setShadowDrwable(Drawable background);

    View getView();

}
