package com.zpf.tool;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by ZPF on 2019/3/27.
 */
public class StatusBarUtil {
    //沉浸式状态栏
    public static void setStatusBarTranslucent(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//判断版本是5.0以上
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//判断版本是4.4以上
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    //获取状态栏高度
    public static int getStatusBarHeight(Context context) {
        int height = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId != 0) {
            height = context.getResources().getDimensionPixelSize(resourceId);
        }
        if (height == 0) {
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,
                    context.getResources().getDisplayMetrics());
        }
        return height;
    }

    public static boolean setBarStatusTextColorStyle(Window window, boolean darkText) {
        if (Build.MANUFACTURER.equalsIgnoreCase("XiaoMi")) {
            if (MIUISetStatusBarLightMode(window, darkText)) {
                SetStatusBarLightMode(window, darkText);
                return true;
            } else {
                return false;
            }
        } else if (Build.MANUFACTURER.equalsIgnoreCase("MeiZu")) {
            if (FlymeSetStatusBarLightMode(window, darkText)) {
                SetStatusBarLightMode(window, darkText);
                return true;
            } else {
                return false;
            }
        }
        return SetStatusBarLightMode(window, darkText);
    }

    public static boolean SetStatusBarLightMode(Window window, boolean darkText) {
        if (window == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = null;
            try {
                controller = window.getInsetsController();
            } catch (Exception e) {
                //华为手机异常
                e.printStackTrace();
            }
            if (controller != null) {
                if (darkText) {
                    controller.setSystemBarsAppearance(
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
                } else {
                    controller.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
                }
                return true;
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int visible = window.getDecorView().getSystemUiVisibility();
            if (darkText) {
                visible = visible | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                window.getDecorView().setSystemUiVisibility(visible);
            } else {
                visible = visible & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                window.getDecorView().setSystemUiVisibility(visible);
            }
            return true;
        }
        return false;
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格，Flyme4.0以上
     */
    private static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {
                //ignore
            }
        }
        return result;
    }

    /**
     * 需要MIUIV6以上
     */
    private static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class<?> clazz = window.getClass();
            try {
                int darkModeFlag;
                Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {
                //ignore
            }
        }
        return result;
    }

}
