package com.zpf.views.button;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.zpf.views.type.IFeedbackView;

/**
 * @author Created by ZPF on 2021/10/21.
 */
public class TouchFeedbackDelegate implements IFeedbackView {
    private float originalAlpha;
    private Drawable originalBackground;
    private float touchDownAlpha;
    private Drawable touchDownBackground;

    public TouchFeedbackDelegate() {
    }

    public TouchFeedbackDelegate(float touchDownAlpha, Drawable touchDownBackground) {
        this.touchDownAlpha = touchDownAlpha;
        this.touchDownBackground = touchDownBackground;
    }

    @Override
    public void setTouchStyle(float alpha, Drawable background) {
        this.touchDownAlpha = alpha;
        this.touchDownBackground = background;
    }

    @Override
    public void onTouch(View view) {
        if (!view.isEnabled()) {
            return;
        }
        originalAlpha = view.getAlpha();
        originalBackground = view.getBackground();
        if (touchDownAlpha > 0 && touchDownAlpha < 1) {
            view.setAlpha(touchDownAlpha);
        }
        if (touchDownBackground != null) {
            view.setBackground(touchDownBackground);
        }
    }

    @Override
    public void onRestore(View view) {
        view.setAlpha(originalAlpha);
        view.setBackground(originalBackground);
    }
}