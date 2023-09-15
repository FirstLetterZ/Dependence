package com.zpf.tool.global;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Looper;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Created by ZPF on 2021/7/7.
 */
public class CentralManager {
    private CentralManager() {
    }

    private static Context appContext;
    private static boolean debuggable;
    private static int debugFlag = 0;
    private static final ConcurrentHashMap<String, ICentralOperator> operatorMap = new ConcurrentHashMap<>();
    private static ICentralOperator realManager;

    public static void init(Context context, ICentralOperator admin) {
        appContext = context.getApplicationContext();
        debuggable = (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        realManager = admin;
    }

    public static void openDebugMode(boolean open) {
        if (debugFlag != 0) {
            return;
        }
        if (open) {
            debugFlag = 1;
        } else {
            debugFlag = -1;
        }
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static boolean isDebugMode() {
        return debugFlag > 0;
    }

    public static boolean debuggable() {
        return debuggable;
    }

    public static void onObjectInit(Object object) {
        if (realManager != null) {
            realManager.onObjectInit(object);
        }
        if (operatorMap.size() > 0) {
            synchronized (operatorMap) {
                if (operatorMap.size() > 0) {
                    for (ICentralOperator config : operatorMap.values()) {
                        config.onObjectInit(object);
                    }
                }
            }
        }
    }

    public static Object invokeMethod(Object object, String methodName, Object... args) {
        Object result = null;
        if (realManager != null) {
            result = realManager.invokeMethod(object, methodName, args);
        }
        if (operatorMap.size() > 0) {
            synchronized (operatorMap) {
                if (operatorMap.size() > 0) {
                    Object temp;
                    for (ICentralOperator config : operatorMap.values()) {
                        temp = config.invokeMethod(object, methodName, args);
                        if (temp != null) {
                            result = temp;
                        }
                    }
                }
            }
        }
        return result;
    }

    public static <T> T getInstance(Class<T> target) {
        T result = null;
        if (realManager != null) {
            result = realManager.getInstance(target);
        }
        if (result == null && operatorMap.size() > 0) {
            synchronized (operatorMap) {
                if (operatorMap.size() > 0) {
                    for (ICentralOperator config : operatorMap.values()) {
                        result = config.getInstance(target);
                        if (result != null) {
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    public static <T> T getInstance(Class<T> target, String qualifier) {
        T result = null;
        if (realManager != null) {
            result = realManager.getInstance(target, qualifier);
        }
        if (result == null && operatorMap.size() > 0) {
            synchronized (operatorMap) {
                if (operatorMap.size() > 0) {
                    for (ICentralOperator config : operatorMap.values()) {
                        result = config.getInstance(target, qualifier);
                        if (result != null) {
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    public static void runOnMainTread(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            MainHandler.get().post(runnable);
        }
    }

    public static void runDelayed(Runnable runnable, long delayMillis) {
        if (runnable == null) {
            return;
        }
        MainHandler.get().postDelayed(runnable, delayMillis);
    }

    public static void cancelRunnable(Runnable runnable) {
        MainHandler.get().removeCallbacks(runnable);
    }

    public static void addOperator(ICentralOperator operator) {
        if (operator != null) {
            operatorMap.put(operator.getId(), operator);
        }
    }

    public static void removeOperator(String operatorId) {
        operatorMap.remove(operatorId);
    }
}
