package com.zpf.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.PopupWindow;

public class TopDropPopup extends PopupWindow {

    public TopDropPopup(Context context) {
        this(context, null, 0, 0);
    }
    public TopDropPopup(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }
    public TopDropPopup(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }
    public TopDropPopup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setAnimationStyle(R.style.AnimTopInTopOut);
    }
}
