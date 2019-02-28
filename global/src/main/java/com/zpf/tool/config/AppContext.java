package com.zpf.tool.config;

import android.annotation.SuppressLint;
import android.app.Application;

import java.lang.reflect.Method;

/**
 * Created by ZPF on 2018/6/13.
 */
public class AppContext {
    private static Application sApplication;

    public static Application get() {
        return sApplication != null ? sApplication : getApplication();
    }

    public static void init(Application application) {
        sApplication = application;
    }

    public static void checkInit(Application application) {
        if (get() == null) {
            sApplication = application;
        }
    }

    @SuppressLint("PrivateApi")
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
        sApplication = application;
        return application;
    }

}
