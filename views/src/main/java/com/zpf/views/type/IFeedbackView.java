package com.zpf.views.type;

import android.graphics.drawable.Drawable;
import android.view.View;

public interface IFeedbackView {
    void setTouchStyle(float alpha, Drawable background);

    void onTouch(View view);

    void onRestore(View view);
}