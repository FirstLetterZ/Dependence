package com.zpf.rvexpand;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class NestedRecyclerView extends RecyclerView {

    private boolean confirmed = false;
    private float downX = 0f;
    private float downY = 0f;
    private int mTouchSlop = 0;

    public NestedRecyclerView(@NonNull Context context) {
        super(context);
        onViewCreate(context);
    }

    public NestedRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        onViewCreate(context);
    }

    public NestedRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onViewCreate(context);
    }

    protected void onViewCreate(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                confirmed = false;
                downX = ev.getRawX();
                downY = ev.getRawY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                LayoutManager manager = getLayoutManager();
                if (manager != null) {
                    float dX;
                    float dY;
                    if (manager.canScrollVertically()) {
                        dX = Math.abs(downX - ev.getRawX());
                        dY = Math.abs(downY - ev.getRawY());
                        if ((dX > mTouchSlop || dY > mTouchSlop) && !confirmed) {
                            confirmed = true;
                            getParent().requestDisallowInterceptTouchEvent(dY > dX);
                        }
                    } else if (manager.canScrollHorizontally()) {
                        dX = Math.abs(downX - ev.getRawX());
                        dY = Math.abs(downY - ev.getRawY());
                        if ((dX > mTouchSlop || dY > mTouchSlop) && !confirmed) {
                            confirmed = true;
                            getParent().requestDisallowInterceptTouchEvent(dX > dY);
                        }
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

}
