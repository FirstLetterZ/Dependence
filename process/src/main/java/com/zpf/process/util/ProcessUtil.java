package com.zpf.process.util;

import android.app.ActivityManager;
import android.content.Context;

/**
 * @author Created by ZPF on 2021/4/8.
 */
public class ProcessUtil {

    public static String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
                break;
            }
        }
        return processName;
    }

    public static boolean isHostProcess(Context context) {
        String processName = getProcessName(context);
        String packageName = context.getPackageName();
        return isHostProcess(packageName, processName);
    }

    public static boolean isHostProcess(String packageName, String processName) {
        return processName == null || processName.equals(packageName);
    }

    public static boolean isServiceProcess(Context context) {
        String processName = getProcessName(context);
        String packageName = context.getPackageName();
        return isServiceProcess(packageName, processName);
    }

    public static boolean isServiceProcess(String packageName, String processName) {
        if (processName == null || processName.equals(packageName)) {
            return false;
        }
        return processName.equals(packageName + ":ps1") ||
                processName.equals(packageName + ":ps2") ||
                processName.equals(packageName + ":ps3") ||
                processName.equals(packageName + ":ps4") ||
                processName.equals(packageName + ":ps5");
    }

}
