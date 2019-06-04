package com.zpf.tool;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by ZPF on 2019/3/27.
 */
public class StatusBarTextUtil {

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int visible = window.getDecorView().getSystemUiVisibility();
            if (darkText) {
                window.getDecorView().setSystemUiVisibility(visible | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
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
