package com.zpf.tool;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Process;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.zpf.tool.config.AppContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by ZPF on 2018/7/26.
 */

public class PublicUtil {

    public static DisplayMetrics getDisplayMetrics() {
        return Resources.getSystem().getDisplayMetrics();
    }

    public static int getColor(int color) {
        return AppContext.get().getResources().getColor(color);
    }

    public static String getString(int id) {
        return AppContext.get().getResources().getString(id);
    }

    public static <T> Class<T> getGenericClass(Class<?> klass) {
        Type type = klass.getGenericSuperclass();
        if (type == null || !(type instanceof ParameterizedType)) return null;
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] types = parameterizedType.getActualTypeArguments();
        if (types == null || types.length == 0) return null;
        return (Class<T>) types[0];
    }

    public static int getVersionCode(Context context) {
        if (context != null) {
            try {
                PackageManager manager = context.getPackageManager();
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                return info.versionCode;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    public static String getVersionName(Context context) {
        String versionName = "0.0.1";
        if (context != null) {
            try {
                PackageManager manager = context.getPackageManager();
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                versionName = info.versionName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return versionName;
    }

    public static String getAppName(Context context) {
        String name = "当前应用";
        if (context != null) {
            try {
                PackageManager manager = context.getPackageManager();
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                int labelRes = info.applicationInfo.labelRes;
                name = context.getResources().getString(labelRes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return name;
    }

    public static boolean isAppInstalled(Context context, String packagename) {
        List<PackageInfo> pInfo = context.getPackageManager().getInstalledPackages(0);
        if (pInfo == null) {
            return false;
        } else {
            for (PackageInfo info : pInfo) {
                if (TextUtils.equals(info.packageName, packagename)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isPackageProces() {
        ActivityManager activityManager = (ActivityManager) AppContext.get().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
            String mainProcessName = AppContext.get().getPackageName();
            int myPid = Process.myPid();
            for (ActivityManager.RunningAppProcessInfo info : appProcessInfoList) {
                if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 将Activity转到前台
     *
     * @param targetClass 目标Activity对应Class
     */
    @SuppressLint("MissingPermission")
    public static void setActivityToTop(Class targetClass) {
        ActivityManager myManager = (ActivityManager) AppContext.get().getSystemService(Context.ACTIVITY_SERVICE);
        if (myManager != null) {
            List<ActivityManager.RunningTaskInfo> runningTaskList = myManager.getRunningTasks(16);
            if (runningTaskList != null && runningTaskList.size() > 0) {
                for (ActivityManager.RunningTaskInfo taskInfo : runningTaskList) {
                    if (TextUtils.equals(taskInfo.topActivity.getClassName(), targetClass.getName())) {
                        myManager.moveTaskToFront(taskInfo.id, 0);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 获取当前APP状态
     *
     * @return -1--未运行；0--在后台；1--在前台；
     */
    public static int getAppState(Context context) {
        int result = -1;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
            String appProcessName = context.getApplicationInfo().processName;
            for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfoList) {
                if (appProcessName.contains(appProcessInfo.processName) || appProcessInfo.processName.contains(appProcessName)) {
                    if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        result = 1;
                    } else {
                        result = 0;
                    }
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 数字小数位数显示处理
     *
     * @param money 金额
     * @param scale 保留位置
     */
    public static BigDecimal scaleNumber(Number money, int scale) {
        BigDecimal amount;
        if (money == null) {
            amount = new BigDecimal(0);
        } else if (money instanceof BigDecimal) {
            amount = (BigDecimal) money;
        } else {
            amount = new BigDecimal(money.doubleValue());
        }
        return amount.setScale(scale, BigDecimal.ROUND_HALF_UP);
    }

    public static String scaleNumberString(Number money, int scale) {
        return scaleNumber(money, scale).toPlainString();
    }

    /**
     * 获取设备识别id
     *
     * @return 如果返回null则代表缺少权限，若返回"unknown"代表获取失败
     */
    @SuppressLint({"HardwareIds", "MissingPermission"})
    public static String getDeviceId(@NonNull Context context) {
        String result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                result = Build.getSerial();
            } else {
                result = Build.SERIAL;
            }
        } else {
            result = Build.SERIAL;
        }
        if (TextUtils.isEmpty(result) || "unknown".equalsIgnoreCase(result)) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                result = telephonyManager.getDeviceId();
            }
        }
        if (TextUtils.isEmpty(result)) {
            result = "unknown";
        }
        return result;
    }
}
