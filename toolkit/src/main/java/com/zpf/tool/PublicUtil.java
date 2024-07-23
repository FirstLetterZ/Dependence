package com.zpf.tool;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

/**
 * Created by ZPF on 2018/7/26.
 */
public class PublicUtil {

    public static DisplayMetrics getDisplayMetrics() {
        return Resources.getSystem().getDisplayMetrics();
    }

    public static <T> Class<T> getGenericClass(Class<?> klass) {
        Type type = klass.getGenericSuperclass();
        if (!(type instanceof ParameterizedType)) return null;
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] types = parameterizedType.getActualTypeArguments();
        if (types.length == 0) return null;
        return (Class<T>) types[0];
    }

    public static int getVersionCode(@NonNull Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Nullable
    public static String getVersionName(@NonNull Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int compareToAppVersion(@NonNull Context context, String versionName) {
        if (versionName == null || versionName.length() < 1) {
            return -1;
        }
        String currentAppVersion = getVersionName(context);
        if (currentAppVersion == null || currentAppVersion.length() < 1) {
            return 1;
        }
        String[] appVersionParts = currentAppVersion.split("\\.");
        String[] compareVersionParts = versionName.split("\\.");
        int maxLen = Math.max(appVersionParts.length, compareVersionParts.length);
        int appVersion;
        int compareVersion;
        for (int i = 0; i < maxLen; i++) {
            appVersion = 0;
            if (i < appVersionParts.length) {
                try {
                    appVersion = Integer.parseInt(appVersionParts[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            compareVersion = 0;
            if (i < compareVersionParts.length) {
                try {
                    compareVersion = Integer.parseInt(compareVersionParts[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (appVersion != compareVersion) {
                return compareVersion - appVersion;
            }
        }
        return 0;
    }

    @Nullable
    public static String getAppName(@NonNull Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = info.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("QueryPermissionsNeeded")
    public static boolean isAppInstalled(@NonNull Context context, @NonNull String packageName) {
        List<PackageInfo> pInfo = context.getPackageManager().getInstalledPackages(0);
        for (PackageInfo info : pInfo) {
            if (TextUtils.equals(info.packageName, packageName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPackageProcess(@NonNull Context context) {
        String mainProcessName = context.getPackageName();
        return mainProcessName.equals(getProcessName(context));
    }

    public static String getProcessName(@NonNull Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
            int myPid = Process.myPid();
            for (ActivityManager.RunningAppProcessInfo info : appProcessInfoList) {
                if (info.pid == myPid) {
                    return info.processName;
                }
            }
        }
        return Build.UNKNOWN;
    }

    /**
     * 将App转到前台
     */
    @SuppressLint("MissingPermission")
    public static boolean moveTaskToTop(@NonNull Context context) {
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (myManager != null) {
            List<ActivityManager.RunningTaskInfo> runningTaskList = myManager.getRunningTasks(16);
            if (runningTaskList != null && runningTaskList.size() > 0) {
                for (ActivityManager.RunningTaskInfo taskInfo : runningTaskList) {
                    ComponentName topName = taskInfo.topActivity;
                    if (topName != null && TextUtils.equals(context.getPackageName(), topName.getPackageName())) {
                        myManager.moveTaskToFront(taskInfo.id, 0);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取当前APP状态
     *
     * @return -1--未运行；0--在后台；1--在前台；
     */
    public static int getAppState(@NonNull Context context) {
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
    public static BigDecimal scaleNumber(@Nullable Number money, int scale) {
        BigDecimal amount;
        if (money == null) {
            amount = BigDecimal.ZERO;
        } else if (money instanceof BigDecimal) {
            amount = (BigDecimal) money;
        } else {
            amount = BigDecimal.valueOf(money.doubleValue());
        }
        return amount.setScale(scale, RoundingMode.HALF_UP);
    }

    public static String scaleNumberString(@Nullable Number money, int scale) {
        return scaleNumber(money, scale).toPlainString();
    }

    public static String stripTrailingZerosString(@Nullable Number money, int scale) {
        BigDecimal amount;
        if (money == null) {
            amount = BigDecimal.ZERO;
        } else if (money instanceof BigDecimal) {
            amount = (BigDecimal) money;
        } else {
            amount = BigDecimal.valueOf(money.doubleValue());
        }
        if (amount.equals(BigDecimal.ZERO)) {
            return "0";
        } else {
            return amount.stripTrailingZeros().toPlainString();
        }
    }

    public static boolean openBySystem(Context context, String url) {
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
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getDeviceId(@NonNull Context context) {
        String result = null;
        boolean missingPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED;
        if (!missingPermission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                result = Build.getSerial();
            } else {
                result = Build.SERIAL;
            }
        }
        if ((TextUtils.isEmpty(result) || Build.UNKNOWN.equalsIgnoreCase(result)) && !missingPermission) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        result = telephonyManager.getImei();
                    } else {
                        result = telephonyManager.getDeviceId();
                    }
                } catch (Exception e) {
                    result = null;
                }
            }
        }
        if (TextUtils.isEmpty(result) || Build.UNKNOWN.equalsIgnoreCase(result)) {
            result = getAndroidId(context);
        }
        if (TextUtils.isEmpty(result) || Build.UNKNOWN.equalsIgnoreCase(result)) {
            result = Build.DISPLAY;
        }
        if (TextUtils.isEmpty(result) || Build.UNKNOWN.equalsIgnoreCase(result)) {
            result = UUID.randomUUID().toString();
        }
        return result;
    }

    @SuppressLint("HardwareIds")
    public static String getAndroidId(Context context) {
        try {
            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            return null;
        }
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

    public static boolean copyToClipboard(Context context, String text) {
        ClipboardManager cmb = ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE));
        if (cmb != null) {
            cmb.setPrimaryClip(ClipData.newPlainText(null, text));
            return true;
        } else {
            return false;
        }
    }

    public static boolean openMarket(Context context) {
        String pkgName = context.getPackageName();
        Uri uri = Uri.parse("market://details?id=" + pkgName);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static float dp2px(int dp) {
        return dp * getDisplayMetrics().density;
    }

    @SuppressLint("MissingPermission")
    public static boolean vibrateOnce(Context context, long milliseconds) {
        Vibrator vibrator = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager manager = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            if (manager != null) {
                vibrator = manager.getDefaultVibrator();
            }
        } else {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
        if (vibrator == null || !vibrator.hasVibrator()) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(milliseconds);
        }
        return true;
    }

    public String upperCaseNumber(int number, boolean isMoney) {
        String[] numbers;
        String[] units;
        if (isMoney) {
            numbers = new String[]{"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
            units = new String[]{"", "拾", "佰", "仟", "万", "十万", "佰万", "仟万", "亿", "拾亿", "佰亿", "仟亿", "万亿"};
        } else {
            numbers = new String[]{"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
            units = new String[]{"", "十", "百", "千", "万", "十万", "百万", "千万", "亿", "十亿", "百亿", "千亿", "万亿"};
        }
        return formatNumber(number, numbers, units, true);
    }

    public static String formatNumber(int value, String[] numberDisplay, String[] unitDisplay, boolean foldZero) throws IndexOutOfBoundsException {
        if (numberDisplay == null) {
            return null;
        }
        int len = numberDisplay.length;
        if (len == 0) {
            return String.valueOf(value);
        }
        StringBuilder builder = new StringBuilder();
        int n = value;
        int m;
        int i = 0;
        String zeroDisplay = numberDisplay[0];
        String temp = zeroDisplay;
        while (n > 0) {
            m = n % len;
            n /= len;
            if (m == 0) {
                if (foldZero) {
                    if (builder.length() > 0 && temp != null && !temp.equals(zeroDisplay)) {
                        temp = zeroDisplay;
                    } else {
                        temp = null;
                    }
                } else {
                    temp = zeroDisplay;
                }
            } else {
                if (i < unitDisplay.length) {
                    temp = unitDisplay[i];
                } else {
                    temp = null;
                }
                if (temp != null) {
                    for (int j = temp.length() - 1; j >= 0; j--) {
                        builder.append(temp.charAt(j));
                    }
                }
                temp = numberDisplay[m];
            }
            if (temp != null) {
                builder.append(temp);
            }
            i++;
        }
        if (builder.length() > 0) {
            return builder.reverse().toString();
        } else if (zeroDisplay != null) {
            return zeroDisplay;
        } else {
            return "";
        }
    }
}
