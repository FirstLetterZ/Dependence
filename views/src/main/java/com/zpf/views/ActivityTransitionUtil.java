package com.zpf.views;

import android.app.Activity;

public class ActivityTransitionUtil {
    public static void onStart(Activity activity, @StackInAnimType int type) {
        switch (type) {
            case StackInAnimType.IN_BOTTOM:
                activity.overridePendingTransition(R.anim.in_from_bottom, 0);
                break;
            case StackInAnimType.IN_LEFT_OUT_RIGHT:
                activity.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                break;
            case StackInAnimType.IN_LEFT:
                activity.overridePendingTransition(R.anim.in_from_left, 0);
                break;
            case StackInAnimType.IN_RIGHT_OUT_LEFT:
                activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            case StackInAnimType.IN_RIGHT:
                activity.overridePendingTransition(R.anim.in_from_right, 0);
                break;
            case StackInAnimType.IN_ZOOM_OUT_ZOOM:
                activity.overridePendingTransition(R.anim.in_center_zoom, R.anim.out_center_zoom);
                break;
            case StackInAnimType.IN_ZOOM:
                activity.overridePendingTransition(R.anim.in_center_zoom, 0);
                break;
            case StackInAnimType.NONE:
                activity.overridePendingTransition(0, 0);
                break;
        }
    }

    public static void onFinish(Activity activity, @StackInAnimType int type) {
        switch (type) {
            case StackInAnimType.IN_BOTTOM:
                activity.overridePendingTransition(0, R.anim.out_to_bottom);
                break;
            case StackInAnimType.IN_LEFT_OUT_RIGHT:
                activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            case StackInAnimType.IN_LEFT:
                activity.overridePendingTransition(0, R.anim.out_to_left);
                break;
            case StackInAnimType.IN_RIGHT_OUT_LEFT:
                activity.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                break;
            case StackInAnimType.IN_RIGHT:
                activity.overridePendingTransition(0, R.anim.out_to_right);
                break;
            case StackInAnimType.IN_ZOOM_OUT_ZOOM:
                activity.overridePendingTransition(R.anim.in_center_zoom, R.anim.out_center_zoom);
                break;
            case StackInAnimType.IN_ZOOM:
                activity.overridePendingTransition(0, R.anim.out_center_zoom);
                break;
            case StackInAnimType.NONE:
                activity.overridePendingTransition(0, 0);
                break;
        }
    }

}