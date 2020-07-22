package com.zpf.api;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import android.view.View;

import java.io.File;

/**
 * Created by ZPF on 2018/7/17.
 */
public interface IconText {

    void setImageOnly(@DrawableRes int id);

    void setImageOnly(Drawable drawable);

    void setImage(@DrawableRes int id, @IntRange(from = 0, to = 3) int location);

    void setImage(Drawable drawable, @IntRange(from = 0, to = 3) int location);

    Drawable[] getDrawables();

    void setImageHeight(int dpHeight);

    int getImageHeight();

    void setImageTintList(@Nullable ColorStateList tint);

    ColorStateList getImageTintList();

    void setImageTintMode(@Nullable PorterDuff.Mode tintMode);

    PorterDuff.Mode getImageTintMode();

    void setIconFont(@StringRes int id);

    void setIconFont(CharSequence text);

    void setText(@StringRes int id);

    void setText(CharSequence text);

    CharSequence getText();

    void setTypefaceFromAsset(String path);

    void setTypefaceFromFile(File file);

    void setTypeface(Typeface typeface);

    void resetTypeface();

    Typeface getCurrentTypeface();

    void setTextSize(int size);

    float getTextSize();

    void setTextColor(@ColorInt int color);

    int getTextColor();

    void setOnClickListener(View.OnClickListener listener);

    void setAutoCheck(boolean autoCheck);

    void setVisibility(int visibility);

    View getView();
}
