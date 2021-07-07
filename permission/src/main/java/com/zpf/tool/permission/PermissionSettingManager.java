package com.zpf.tool.permission;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PermissionSettingManager {
    private PermissionSettingManager() {
    }

    public static PermissionSettingManager get() {
        return Instance.instance;
    }

    private static class Instance {
        private static final PermissionSettingManager instance = new PermissionSettingManager();
    }

    public void jumpToPermissionSetting(Context context) {
        Intent intent = null;
        String packageName = context.getApplicationInfo().packageName;
        if (!TextUtils.isEmpty(Build.MANUFACTURER)) {
            if (Build.MANUFACTURER.equalsIgnoreCase("HuaWei")) {//华为
                intent = getHuaWeiIntent(packageName);
            } else if (Build.MANUFACTURER.equalsIgnoreCase("SamSung")) {//三星
            } else if (Build.MANUFACTURER.equalsIgnoreCase("XiaoMi")) {//小米
                intent = getXiaoMiIntent(packageName);
            } else if (Build.MANUFACTURER.equalsIgnoreCase("OPPO")) {
                intent = getOPPOIntent(packageName);
            } else if (Build.MANUFACTURER.equalsIgnoreCase("MeiZu")) {//魅族
                intent = getMeizuIntent(packageName);
            } else if (Build.MANUFACTURER.equalsIgnoreCase("Sony")) {//索尼
                intent = getSony(packageName);
            } else if (Build.MANUFACTURER.equalsIgnoreCase("LG")) {
                intent = getLGIntent(packageName);
            } else if (Build.MANUFACTURER.equalsIgnoreCase("vivo")) {
            } else if (Build.MANUFACTURER.equalsIgnoreCase("Letv")) {//乐视
                intent = getLetvIntent(packageName);
            } else if (Build.MANUFACTURER.equalsIgnoreCase("ZTE")) {//中兴
            } else if (Build.MANUFACTURER.equalsIgnoreCase("YuLong")) {//酷派
            } else if (Build.MANUFACTURER.equalsIgnoreCase("LENOVO")) {//联想
            }
        }
        if (intent == null) {
            jump(getAppInfoIntent(context), context, 1);
        } else {
            jump(intent, context, 2);
        }
    }

    private void jump(Intent intent, Context context, int level) {
        if (intent != null) {
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                switch (level) {
                    case 2:
                        jump(getAppInfoIntent(context), context, 1);
                        break;
                    case 1:
                        jump(getSystemConfig(), context, 0);
                        break;
                }
            }
        }
    }

    public void jumpToNoticeSetting(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.getApplicationInfo().uid);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        }
        context.startActivity(intent);
    }

    public void jumpToAppSetting(Context context) {
        context.startActivity(getAppInfoIntent(context));
    }

    public void jumpToSystemSetting(Context context) {
        context.startActivity(getSystemConfig());
    }

    /**
     * 应用信息界面
     */
    private Intent getAppInfoIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return localIntent;
    }

    /**
     * 系统设置界面
     */
    private Intent getSystemConfig() {
        return new Intent(Settings.ACTION_SETTINGS);
    }

    private Intent getHuaWeiIntent(String packageName) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", packageName);
        ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
        intent.setComponent(comp);
        return intent;
    }

    private Intent getMeizuIntent(String packageName) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", packageName);
        return intent;
    }

    private Intent getXiaoMiIntent(String packageName) {
        String rom = getMiuiVersion();
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        if ("V5".equals(rom)) {
            Uri packageURI = Uri.parse("package:" + packageName);
            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
        } else if ("V6".equals(rom) || "V7".equals(rom)) {
            intent.setAction("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            intent.putExtra("extra_pkgname", packageName);
        } else if ("V8".equals(rom) || "V9".equals(rom)) {
            intent.setAction("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            intent.putExtra("extra_pkgname", packageName);
        } else {
            intent = null;
        }
        return intent;
    }

    private Intent getSony(String packageName) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", packageName);
        ComponentName comp = new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity");
        intent.setComponent(comp);
        return intent;
    }

    private Intent getOPPOIntent(String packageName) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", packageName);
        ComponentName comp = new ComponentName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
        intent.setComponent(comp);
        return intent;
    }

    private Intent getLGIntent(String packageName) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", packageName);
        ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
        intent.setComponent(comp);
        return intent;
    }

    private Intent getLetvIntent(String packageName) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", packageName);
        ComponentName comp = new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps");
        intent.setComponent(comp);
        return intent;
    }

    /**
     * 只能打开到自带安全软件
     */
    private Intent get360Intent(String packageName) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", packageName);
        ComponentName comp = new ComponentName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
        intent.setComponent(comp);
        return intent;
    }

    private String getMiuiVersion() {
        String propName = "ro.miui.ui.version.name";
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(
                    new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }

}