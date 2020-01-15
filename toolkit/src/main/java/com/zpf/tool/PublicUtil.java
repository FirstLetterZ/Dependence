package com.zpf.tool;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.zpf.tool.config.AppContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
     * @param taskId 目标Activity对应taskId
     */
    public static void moveTaskToTop(int taskId) {
        ActivityManager myManager = (ActivityManager) AppContext.get().getSystemService(Context.ACTIVITY_SERVICE);
        if (myManager != null) {
            List<ActivityManager.RunningTaskInfo> runningTaskList = myManager.getRunningTasks(16);
            if (runningTaskList != null && runningTaskList.size() > 0) {
                for (ActivityManager.RunningTaskInfo taskInfo : runningTaskList) {
                    if (taskInfo.id == taskId) {
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

    public static boolean openBrowser(Context context, String url) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void jumpToAppStore(Context context) {
        String packageName = context.getPackageName();
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    /**
     * 获取设备识别id
     *
     * @return 如果返回null则代表缺少权限，若返回"unknown"代表获取失败
     */
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

    /**
     * 修复部分机型 AssetManager.finalize() 引发超时崩溃的问题
     */
    public static boolean fixAssetManager(String[] keyWords) {
        if (keyWords != null && keyWords.length > 0) {
            String device = Build.BRAND + " " + Build.MODEL;
            for (String w : keyWords) {
                if (w == null || w.length() == 0) {
                    continue;
                }
                if (device.contains(w) || w.contains(device)) {
                    try {
                        // 关闭掉FinalizerWatchdogDaemon
                        Class clazz = Class.forName("java.lang.Daemons\\$FinalizerWatchdogDaemon");
                        Method method = clazz.getSuperclass().getDeclaredMethod("stop");
                        method.setAccessible(true);
                        Field field = clazz.getDeclaredField("INSTANCE");
                        field.setAccessible(true);
                        method.invoke(field.get(null));
                        return true;
                    } catch (Throwable e) {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    public static String getMetaDataValue(@NonNull Context context, @NonNull String metaDataName) {
        String result = null;
        PackageManager manager = context.getPackageManager();
        ApplicationInfo info = null;
        try {
            info = manager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            //
        }
        if (info != null && info.metaData != null) {
            result = info.metaData.getString(metaDataName);
        }
        return result;
    }

}
