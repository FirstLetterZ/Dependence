package com.zpf.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

public class StatusBar extends View {

    private int statusBarHeight;

    public StatusBar(Context context) {
        this(context, null, 0);
    }

    public StatusBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        statusBarHeight = getStatusBarHeight(context);
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (params != null) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = statusBarHeight;
        }
        super.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int h = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        if (h != statusBarHeight) {
            getLayoutParams().height = statusBarHeight;
            setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                    statusBarHeight);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    //获取状态栏高度
    public int getStatusBarHeight(Context context) {
        int height = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId != 0) {
            height = context.getResources().getDimensionPixelSize(resourceId);
        }
        if (height == 0) {
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,
                    context.getResources().getDisplayMetrics());
        }
        return height;
    }
}
