package com.zpf.views;

import android.content.Context;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

/**
 * @author Created by ZPF on 2021/11/24.
 */
public class TestPopupView extends ViewGroup {

    public TestPopupView(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        ViewParent vp = getParent();
        if (vp instanceof FrameLayout) {
            bringToFront();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    public void setCancelable(boolean flag) {

    }

    public void setCanceledOnTouchOutside(boolean flag) {

    }

    public void setOutsideTouchable(boolean flag) {

    }

    public void setAnimType(int type) {

    }

    public void show() {

    }
    
    public void showAsTopDown(boolean belowTopBar) {

    }


    public void showAsBottomUp() {

    }


    public void showAtAnchor(View anchor, int xoff, int yoff, int align) {

    }

    public void showAtLocation(int x, int y) {

    }
}
