package com.zpf.tool;

import android.app.Application;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zpf.tool.config.MainHandler;

public class DefToaster implements IToaster {
    private Toast mToast;
    private TextView mText;
    private LinearLayout mToastLayout;
    private WindowManager.LayoutParams mWindowParams;
    private static volatile long lastShow = 0;

    public DefToaster(Application context) {
        mToast = new Toast(context);
        float density = context.getResources().getDisplayMetrics().density;

        mToastLayout = new LinearLayout(context);
        mWindowParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        mToastLayout.setLayoutParams(mWindowParams);
        mToastLayout.setPadding((int) (16 * density), (int) (16 * density), (int) (16 * density), (int) (16 * density));
        mToastLayout.setGravity(Gravity.CENTER);
        mText = new TextView(context);
        mText.setMinWidth((int) (80 * density));
        mText.setPadding((int) (12 * density), (int) (8 * density), (int) (12 * density), (int) (8 * density));
        mText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        mText.setTextColor(Color.WHITE);
        mText.setGravity(Gravity.CENTER_HORIZONTAL);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(12 * density);
        gradientDrawable.setColor(Color.parseColor("#aa000000"));
        mText.setBackground(gradientDrawable);
        mToastLayout.addView(mText);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setView(mToastLayout);
    }

    @Override
    public View getToastView() {
        return mText;
    }

    @Override
    public ViewGroup getLayout() {
        return mToastLayout;
    }

    @Override
    public WindowManager.LayoutParams getWindowParams() {
        return mWindowParams;
    }

    @Override
    public void showToast(final CharSequence text) {
        MainHandler.runOnMainTread(new Runnable() {
            @Override
            public void run() {
                mText.setText(text);
                long duration = (mToast.getDuration() == Toast.LENGTH_LONG ? 7000 : 4000);
                if (System.currentTimeMillis() - lastShow > duration) {
                    lastShow = System.currentTimeMillis();
                    mToast.show();
                } else if (System.currentTimeMillis() - lastShow >= 0.5 * duration) {
                    mToast.cancel();
                    lastShow = System.currentTimeMillis();
                    MainHandler.get().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mToast.show();
                        }
                    }, 10);
                }
            }
        });
    }

    @Override
    public void onDismiss() {

    }

    @Override
    public boolean isUsable() {
        return true;
    }
}
