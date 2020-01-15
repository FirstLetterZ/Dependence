package com.zpf.tool.config;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.pm.ApplicationInfo;

import java.lang.reflect.Method;

/**
 * Created by ZPF on 2018/6/13.
 */
public class AppContext {
    private static volatile Application sApplication;

    public static Application get() {
        if (sApplication == null) {
            synchronized (AppContext.class) {
                if (sApplication == null) {
                    sApplication = getApplication();
                }
            }
        }
        return sApplication;
    }

    public static void init(Application application) {
        sApplication = application;
    }

    public static void checkInit(Application application) {
        if (sApplication == null) {
            sApplication = application;
        }
    }

    @SuppressLint({"PrivateApi", "DiscouragedPrivateApi"})
    private static Application getApplication() {
        Application application = null;
        Method method;
        try {
            method = Class.forName("android.app.AppGlobals").getDeclaredMethod("getInitialApplication");
            method.setAccessible(true);
            application = (Application) method.invoke(null);
        } catch (Exception e) {
            try {
                method = Class.forName("android.app.ActivityThread").getDeclaredMethod("currentApplication");
                method.setAccessible(true);
                application = (Application) method.invoke(null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return application;
    }

    public static boolean isDebuggable() {
        try {
            return (get().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

}
