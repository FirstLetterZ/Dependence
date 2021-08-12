package com.zpf.process;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;

import com.zpf.process.aidl.IServiceHandler;
import com.zpf.process.api.IProcessInitCallback;
import com.zpf.process.api.IResumeService;
import com.zpf.process.manager.HostManagerImpl;
import com.zpf.process.manager.ServiceManagerImpl;
import com.zpf.process.model.ProcessRecord;
import com.zpf.process.util.ProcessUtil;

import java.util.LinkedList;

/**
 * @author Created by ZPF on 2021/4/8.
 */
public class ProcessAdmin {
    public final static String NO_HANDLER_PREFIX = "unfound_event_handler:";
    public static boolean DEBUGGABLE = false;
    private static Context appContext;
    private static volatile ProcessAdmin sdkInstance;
    private final ProcessRecord processRecord;

    private ProcessAdmin(int size) {
        if (size < 1 || size > 5) {
            throw new IllegalArgumentException("service size must in 1 to 5,current is " + size);
        }
        processRecord = new ProcessRecord(size);
    }

    public static void initConfig(Context context, Bundle config, int serviceSize, IProcessInitCallback initCallback) {
        if (context == null) {
            return;
        }
        DEBUGGABLE = (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        if (ProcessUtil.isHostProcess(context)) {
            appContext = context.getApplicationContext();
            if (sdkInstance == null) {
                synchronized (ProcessAdmin.class) {
                    if (sdkInstance == null) {
                        sdkInstance = new ProcessAdmin(serviceSize);
                    }
                }
            }
            sdkInstance.processRecord.initConfig(context, config);
            HostManagerImpl hostManager = HostManagerImpl.get(context);
            if (initCallback != null) {
                initCallback.onHostInit(hostManager);
            }
        } else {
            ServiceManagerImpl serviceManager = ServiceManagerImpl.get(context);
            if (serviceManager != null) {
                serviceManager.bindHost();
                if (initCallback != null) {
                    initCallback.onServiceInit(serviceManager);
                }
            }
        }
    }

    public static int getServiceSize() {
        if (sdkInstance == null) {
            return 0;
        }
        return sdkInstance.processRecord.getServiceSize();
    }

    public static void callService(String clientId, Bundle params) throws RemoteException, RuntimeException {
        if (sdkInstance == null) {
            throw new RuntimeException("请完成初始化并在主进程内调用");
        }
        sdkInstance.processRecord.callService(appContext, clientId, params);
    }

    public static IServiceHandler getServiceHandler(String clientId) {
        if (sdkInstance == null) {
            return null;
        }
        return sdkInstance.processRecord.findServiceHandler(appContext, clientId);
    }

    public static void updateServiceHandler(int serviceId, IServiceHandler serviceHandler) {
        if (sdkInstance == null) {
            return;
        }
        sdkInstance.processRecord.updateServiceHandler(serviceId, serviceHandler);
    }

    public static boolean stopAll() {
        if (sdkInstance == null) {
            return false;
        }
        sdkInstance.processRecord.clear();
        return true;
    }

    //应该在主进程，且初始化之后设置
    //TODO 缺少低版本的解决方案
    public static void setResumeHelper(IResumeService resumeService) {
        if (sdkInstance == null || appContext == null || resumeService == null) {
            return;
        }
        ActivityManager manager = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) {
            return;
        }
        LinkedList<Integer> list = new LinkedList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (ActivityManager.AppTask appTask : manager.getAppTasks()) {
                ActivityManager.RecentTaskInfo taskInfo = appTask.getTaskInfo();
                if (taskInfo.numActivities > 0) {
                    continue;
                }
                ComponentName base = taskInfo.baseIntent.getComponent();
                if (base == null) {
                    continue;
                }
                list.add(resumeService.conversionServiceId(base.getClassName()));
            }
        }
        if (list.size() > 0) {
            sdkInstance.processRecord.resumeService(appContext, list);
        }
    }
}