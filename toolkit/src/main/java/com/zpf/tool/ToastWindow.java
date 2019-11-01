package com.zpf.tool;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class ToastWindow implements IToaster {
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams layoutParams;
    private LinearLayout mLayout;
    private TextView defText;
    private volatile boolean destroyed;
    private Thread thread = null;
    private BlockingQueue<CharSequence> workQueue = new LinkedBlockingQueue<>(128);
    public int animTime = 300;
    public int showTime = 3000;
    private boolean hasAdd = false;
    private Application application;

    public ToastWindow(Application application) {
        this.application = application;
        mWindowManager = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);
        float density = Resources.getSystem().getDisplayMetrics().density;

        defText = new TextView(application);
        defText.setMinWidth((int) (80 * density));
        defText.setPadding((int) (12 * density), (int) (8 * density), (int) (12 * density), (int) (8 * density));
        defText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        defText.setTextColor(Color.WHITE);
        defText.setGravity(Gravity.CENTER_HORIZONTAL);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(12 * density);
        gradientDrawable.setColor(Color.parseColor("#aa000000"));
        defText.setBackground(gradientDrawable);

        mLayout = new LinearLayout(application);
        mLayout.setEnabled(false);
        mLayout.setFocusable(false);
        mLayout.addView(defText);
        mLayout.setPadding((int) (16 * density), (int) (16 * density), (int) (16 * density), (int) (16 * density));
        mLayout.setVisibility(View.GONE);
        mLayout.setGravity(Gravity.CENTER);

        layoutParams = new WindowManager.LayoutParams();
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        mWindowManager.addView(mLayout, layoutParams);
        init();
    }

    public boolean init() {
        if (!hasAdd) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!Settings.canDrawOverlays(this.application)) {
                    return false;
                }
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
            try {
                mWindowManager.addView(mLayout, layoutParams);
            } catch (Exception e) {
                return false;
            }
            hasAdd = true;
        }
        if (thread != null) {
            if (!thread.isAlive()) {
                try {
                    thread.interrupt();
                } catch (Exception e) {
                    //
                } finally {
                    thread = null;
                }
            }
        }
        if (thread == null) {
            thread = createThread();
            thread.start();
        }
        destroyed = false;
        return true;
    }

    public void destroy() {
        destroyed = true;
        thread = null;
    }

    private Thread createThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!destroyed) {
                        final CharSequence text = workQueue.poll(showTime, TimeUnit.MILLISECONDS);
                        if (text == null) {
                            mLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    onDismiss();
                                }
                            });
                            continue;
                        }
                        mLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mLayout.getVisibility() != View.VISIBLE) {
                                    mLayout.setVisibility(View.VISIBLE);
                                    Animation animation = new AlphaAnimation(0, 1);
                                    animation.setDuration(animTime);
                                    defText.startAnimation(animation);
                                }
                                defText.setText(text);
                            }
                        });
                        LockSupport.parkNanos(ToastWindow.this, TimeUnit.MILLISECONDS.toNanos(100));
                    }
                } catch (Exception e) {
                    init();
                }
            }
        });
    }

    @Override
    public View getToastView() {
        return defText;
    }

    @Override
    public ViewGroup getLayout() {
        return mLayout;
    }

    @Override
    public WindowManager.LayoutParams getWindowParams() {
        return layoutParams;
    }

    @Override
    public void showToast(CharSequence text) {
        if (destroyed) {
            return;
        }
        workQueue.offer(text);
    }

    @Override
    public void onDismiss() {
        Animation animation = new AlphaAnimation(1, 0);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation.setDuration(animTime);
        defText.startAnimation(animation);
    }

}