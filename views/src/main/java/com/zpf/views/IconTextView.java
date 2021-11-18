package com.zpf.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.zpf.views.type.IconText;

import java.io.File;

/**
 * Created by ZPF on 2018/6/25.
 */
public class IconTextView extends TextView implements IconText {
    private Typeface mOriginalTypeface;
    private Typeface mTypeface;
    private int imageHeight;
    private final String defaultTtfFilePath = "iconfont/iconfont.ttf";//默认的ttf文件位置
    private boolean autoCheck = true;
    private int mCurrentColor;
    private ColorStateList mTint;
    private PorterDuff.Mode mCurrentMode = PorterDuff.Mode.SRC_IN;
    private Drawable[] mDrawableArray;

    public IconTextView(Context context) {
        super(context);
        initOriginalParams(context, null);
    }

    public IconTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initOriginalParams(context, attrs);
    }

    public IconTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initOriginalParams(context, attrs);
    }

    protected void initOriginalParams(Context context, @Nullable AttributeSet attrs) {
        mOriginalTypeface = getTypeface();
        mTypeface = mOriginalTypeface;
        setIncludeFontPadding(false);
    }

    @Override
    public void setImageOnly(int id) {
        Drawable drawable = null;
        try {
            drawable = getContext().getResources().getDrawable(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setImageOnly(drawable);
    }

    @Override
    public void setImageOnly(Drawable drawable) {
        setText(null);
        if (drawable != null && drawable.getIntrinsicHeight() > 0) {
            if (imageHeight > 0) {
                zoom(drawable);
                setCompoundDrawables(drawable, null, null, null);
            } else {
                setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }
        }
    }

    @Override
    public void setImage(@DrawableRes int id, @IntRange(from = 0, to = 3) int location) {
        if (location < 0 || location > 3) {
            return;
        }
        Drawable drawable = null;
        try {
            drawable = getContext().getResources().getDrawable(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setImage(drawable, location);
    }

    @Override
    public void setImage(Drawable drawable, @IntRange(from = 0, to = 3) int location) {
        if (location < 0 || location > 3) {
            return;
        }
        zoom(drawable);
        Drawable[] drawables = getCompoundDrawables();
        drawables[location] = drawable;
        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

    @Override
    public Drawable[] getDrawables() {
        return getCompoundDrawables();
    }

    @Override
    public void setImageHeight(int dpHeight) {
        imageHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpHeight,
                getResources().getDisplayMetrics());
    }

    @Override
    public int getImageHeight() {
        return imageHeight;
    }

    @Override
    public void setImageTintList(@Nullable ColorStateList tint) {
        mTint = tint;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setCompoundDrawableTintList(tint);
        } else {
            updateTint();
        }
    }

    @Override
    public ColorStateList getImageTintList() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getCompoundDrawableTintList();
        } else {
            return mTint;
        }
    }

    @Override
    public void setImageTintMode(@Nullable PorterDuff.Mode tintMode) {
        mCurrentMode = tintMode;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setCompoundDrawableTintMode(tintMode);
        } else {
            updateTint();
        }
    }

    @Override
    public PorterDuff.Mode getImageTintMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getCompoundDrawableTintMode();
        } else {
            return mCurrentMode;
        }
    }

    @Override
    public void setIconFont(@StringRes int id) {
        if (mTypeface == mOriginalTypeface) {
            setTypefaceFromAsset(defaultTtfFilePath);
        }
        super.setText(id);
    }

    @Override
    public void setIconFont(CharSequence text) {
        if (mTypeface == mOriginalTypeface) {
            setTypefaceFromAsset(defaultTtfFilePath);
        }
        super.setText(text);
    }

    @Override
    public void setTypefaceFromAsset(String path) {
        if (!TextUtils.isEmpty(path)) {
            try {
                mTypeface = Typeface.createFromAsset(getContext().getAssets(), path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setTypeface(mTypeface);
    }

    @Override
    public void setTypefaceFromFile(File file) {
        if (file != null && file.exists()) {
            try {
                mTypeface = Typeface.createFromFile(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setTypeface(mTypeface);
    }

    @Override
    public void resetTypeface() {
        mTypeface = mOriginalTypeface;
        setTypeface(mTypeface);
    }

    @Override
    public Typeface getCurrentTypeface() {
        return mTypeface;
    }

    @Override
    public void setTextSize(int size) {
        super.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
    }

    @Override
    public int getTextColor() {
        return super.getCurrentTextColor();
    }

    @Override
    public void setAutoCheck(boolean autoCheck) {
        this.autoCheck = autoCheck;
    }

    @Override
    public void setVisibility(int visibility) {
        autoCheck = false;
        super.setVisibility(visibility);
    }

    @Override
    public View getView() {
        return this;
    }

    /**
     * 缩放
     */
    private void zoom(Drawable drawable) {
        if (drawable != null && drawable.getIntrinsicHeight() > 0) {
            float height = imageHeight;
            if (height <= 0) {
                height = getTextSize();
            }
            if (drawable.getIntrinsicHeight() != height) {
                float b = height / drawable.getIntrinsicHeight();
                drawable.setBounds(0, 0, (int) (b * drawable.getIntrinsicWidth()), (int) height);
            } else {
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            }
        }
    }

    @Override
    public void setCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
        if (mDrawableArray == null) {
            mDrawableArray = new Drawable[]{null, null, null, null};
        }
        mDrawableArray[0] = left;
        mDrawableArray[1] = top;
        mDrawableArray[2] = right;
        mDrawableArray[3] = bottom;
        updateTint();
        super.setCompoundDrawables(left, top, right, bottom);
    }

    private void updateTint() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (mDrawableArray == null) {
                mDrawableArray = new Drawable[]{null, null, null, null};
                return;
            }
            for (Drawable d : mDrawableArray) {
                if (d != null) {
                    if (mCurrentMode == null || mTint == null) {
                        d.clearColorFilter();
                    } else {
                        int color = mTint.getColorForState(d.getState(), mTint.getDefaultColor());
                        if (mCurrentColor != color) {
                            d.setColorFilter(color, mCurrentMode);
                            mCurrentColor = color;
                        }
                    }
                }
            }
        }
    }

    public void checkViewShow() {
        if (!autoCheck) {
            return;
        }
        boolean isEmpty = TextUtils.isEmpty(getText());
        if (!isEmpty) {
            if (getVisibility() != View.VISIBLE) {
                super.setVisibility(View.VISIBLE);
            }
        } else {
            Drawable[] drawables = getCompoundDrawables();
            for (Drawable d : drawables) {
                if (d != null) {
                    isEmpty = false;
                    break;
                }
            }
            if (isEmpty) {
                if (getVisibility() != View.GONE) {
                    super.setVisibility(View.GONE);
                }
            } else {
                if (getVisibility() != View.VISIBLE) {
                    super.setVisibility(View.VISIBLE);
                }
            }
        }
    }

}
