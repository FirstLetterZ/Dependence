package com.zpf.views.stretchy;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.RequiresApi;

public class ScrollLoadLayout extends StretchyScrollLayout {
    public static final int STATE_LOADING = 4;

    protected final boolean[] loadEnable = new boolean[]{false, false, false, false};
    public ScrollLoadLayout(Context context) {
        super(context);
    }
    public ScrollLoadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ScrollLoadLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ScrollLoadLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected int computeHorizontallyRollbackEnd(int currentScroll) {
        if (currentScroll < -boundaryWidths[0] / 2) {
            if (boundaryStates[0] == STATE_LOADING) {
                return -boundaryWidths[0];
            }
        } else if (currentScroll > boundaryWidths[2] / 2) {
            if (boundaryStates[2] == STATE_LOADING) {
                return boundaryWidths[2];
            }
        }
        return 0;
    }

    @Override
    protected int computeVerticallyRollbackEnd(int currentScroll) {
        if (currentScroll < -boundaryWidths[1] / 2) {
            if (boundaryStates[1] == STATE_LOADING) {
                return -boundaryWidths[1];
            }
        } else if (currentScroll > boundaryWidths[3] / 2) {
            if (boundaryStates[3] == STATE_LOADING) {
                return boundaryWidths[3];
            }
        }
        return 0;
    }

    @Override
    protected int computeState(int location, int scrollValue, int maxScrollValue) {
        int oldState = boundaryStates[location];
        if (oldState == STATE_LOADING) {
            return oldState;
        }
        if (loadEnable[location] && oldState == STATE_OVER_BOUNDARY && rollBackAnimator != null) {
            int stateSize = boundaryStates.length;
            int oppositeDirectionState = boundaryStates[(location + stateSize / 2) % stateSize];
            if (oppositeDirectionState != STATE_LOADING) {
                return STATE_LOADING;
            }
        }
        return super.computeState(oldState, scrollValue, maxScrollValue);
    }

    public void setLoadEnable(boolean[] flags) {
        if (flags.length != loadEnable.length) {
            return;
        }
        System.arraycopy(flags, 0, loadEnable, 0, loadEnable.length);
    }

    public void setLoadEnable(boolean enable, int location) {
        if (location < 0 || location >= boundaryWidths.length) {
            return;
        }
        int oldState = boundaryStates[location];
        loadEnable[location] = enable;
    }

    public void finishLoading(int location) {
        if (location < 0 || location >= boundaryStates.length) {
            return;
        }
        int oldState = boundaryStates[location];
        if (oldState == STATE_LOADING) {
            boundaryStates[location] = STATE_REVERTING;
            onStateChanged(boundaryStates[0], boundaryStates[1], boundaryStates[2], boundaryStates[3]);
            startRollback();
        }
    }

}
