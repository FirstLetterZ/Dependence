package com.zpf.views.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class OverlayHelper {
    private final WindowManager.LayoutParams windowParams;
    private final WindowManager windowManager;
    private float touchX = 0;
    private float touchY = 0;
    private float startX = 0;
    private float startY = 0;
    private boolean moved = false;
    private float viewAlpha = 1f;
    private final View.OnTouchListener touchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                if (v.getAlpha() != 1f) {
                    v.setAlpha(1f);
                }
                touchX = event.getRawX();
                touchY = event.getRawY();
                startX = windowParams.x;
                startY = windowParams.y;
                moved = false;
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
                v.setAlpha(viewAlpha);
                return moved;
            } else if (action == MotionEvent.ACTION_MOVE) {
                float dx = event.getRawX() - touchX;
                float dy = event.getRawY() - touchY;
                if (!moved && Math.abs(dx * dy) > 6) {
                    moved = true;
                }
                windowParams.x = (int) (startX + dx);
                windowParams.y = (int) (startY + dy);
                windowManager.updateViewLayout(v, windowParams);
            }
            return false;
        }
    };

    public OverlayHelper(Context context) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            windowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            windowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        windowParams.format = PixelFormat.RGBA_8888;
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        windowParams.gravity = Gravity.START | Gravity.TOP;
    }

    public boolean addToWindow(View view, int x, int y, boolean openSetting) {
        Context context = view.getContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean canDrawOverlays = Settings.canDrawOverlays(context);
            if (!canDrawOverlays) {
                if (openSetting) {
                    Intent serviceIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + context.getPackageName()));
                    serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(serviceIntent);
                }
                return false;
            }
        }
        view.setOnTouchListener(touchListener);
        if (view.getParent() != null) {
            return true;
        }
        viewAlpha = view.getAlpha();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            float density = context.getResources().getDisplayMetrics().density;
            windowParams.height = (int) (40 * density);
            windowParams.width = (int) (40 * density);
        } else {
            windowParams.height = layoutParams.height;
            windowParams.width = layoutParams.width;
        }
        windowParams.x = x;
        windowParams.y = y;
        try {
            windowManager.addView(view, windowParams);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void removeFromWindow(View view) {
        windowManager.removeView(view);
    }

}
