package com.zpf.tool.permission;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.tool.permission.interfaces.IPermissionChecker;
import com.zpf.tool.permission.interfaces.IPermissionResultListener;
import com.zpf.tool.permission.model.ActivityPermissionChecker;
import com.zpf.tool.permission.model.FragmentPermissionChecker;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Created by ZPF on 2021/6/18.
 */
public class PermissionManager {
    public static final int REQ_PERMISSION_CODE = 10001;
    public static final String PERMISSION_RECORD = "app_permission_record_file";
    private WeakReference<IPermissionResultListener> defCallBack = null;
    private SparseArray<WeakReference<IPermissionResultListener>> tempCallBacks = new SparseArray<>();
    private final HashMap<Class<?>, IPermissionChecker> checkerMap = new HashMap<>();

    private PermissionManager() {
        addChecker(Activity.class, new ActivityPermissionChecker());
        addChecker(Fragment.class, new FragmentPermissionChecker());
    }

    public static PermissionManager get() {
        return Instance.instance;
    }

    private static class Instance {
        private static final PermissionManager instance = new PermissionManager();
    }

    public void addChecker(Class<?> clz, IPermissionChecker checker) {
        checkerMap.put(clz, checker);
    }

    public void removeChecker(Class<?> clz) {
        checkerMap.remove(clz);
    }

    public void setDefResultListener(IPermissionResultListener listener) {
        if (listener == null) {
            defCallBack = null;
        } else {
            defCallBack = new WeakReference<>(listener);
        }
    }

    public int hasPermission(@NonNull Object requester, @NonNull String... permissions) {
        if (Build.VERSION.SDK_INT >= 23) {
            Context context = null;
            if (requester instanceof Context) {
                context = (Context) requester;
            } else {
                for (IPermissionChecker c : checkerMap.values()) {
                    if (c.shouldHandleRequest(requester)) {
                        context = c.getContext();
                        break;
                    }
                }
            }
            if (context == null) {
                return -1;
            }
            int result = 0;
            for (String per : permissions) {
                if (context.checkPermission(per, Process.myPid(), Process.myUid()) != PackageManager.PERMISSION_GRANTED) {
                    result++;
                }
            }
            return result;
        } else {
            return permissions.length;
        }
    }

    public boolean checkPermission(@NonNull Object requester, @NonNull String... permissions) {
        return checkPermission(requester, REQ_PERMISSION_CODE, null, permissions);
    }

    public boolean checkPermission(@NonNull Object requester, @Nullable IPermissionResultListener listener, @NonNull String... permissions) {
        return checkPermission(requester, REQ_PERMISSION_CODE, listener, permissions);
    }

    public boolean checkPermission(@NonNull Object requester, int requestCode, @NonNull String... permissions) {
        return checkPermission(requester, requestCode, null, permissions);
    }

    public boolean checkPermission(@NonNull Object requester, int requestCode, @Nullable IPermissionResultListener listener, @NonNull String... permissions) {
        if (Build.VERSION.SDK_INT >= 23) {
            IPermissionChecker checker = null;
            for (IPermissionChecker c : checkerMap.values()) {
                if (c.shouldHandleRequest(requester)) {
                    checker = c;
                    break;
                }
            }
            if (checker == null) {
                return false;
            }
            Context context = checker.getContext();
            if (context == null) {
                //已回收
                return false;
            }
            List<String> missPermissionList = new ArrayList<>();
            SharedPreferences.Editor editor = null;
            SharedPreferences sp = context.getSharedPreferences(PERMISSION_RECORD, 0);
            boolean showCustomRationale = false;
            for (String per : permissions) {
                if (context.checkPermission(per, Process.myPid(), Process.myUid()) != PackageManager.PERMISSION_GRANTED) {
                    if (sp != null && !sp.getBoolean(per, false)) {
                        if (editor == null) {
                            editor = sp.edit();
                        }
                        editor.putBoolean(per, true);
                    } else if (!checker.shouldShowRequestPermissionRationale(per)) {
                        showCustomRationale = true;
                    }
                    missPermissionList.add(per);
                }
            }
            if (editor != null) {
                editor.commit();
            }
            if (showCustomRationale) {
                callback(listener, false, requestCode, permissions, missPermissionList);
                return false;
            } else if (missPermissionList.size() > 0) {
                int size = missPermissionList.size();
                checker.requestPermissions(missPermissionList.toArray(new String[size]), requestCode);
                return false;
            } else {
                callback(listener, false, requestCode, permissions, null);
                return true;
            }
        } else {
            callback(listener, false, requestCode, permissions, null);
            return true;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        List<String> missPermissionList = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                if (permissions.length > i) {
                    missPermissionList.add(permissions[i]);
                }
            }
        }
        IPermissionResultListener listener = null;
        WeakReference<IPermissionResultListener> weakReference = tempCallBacks.get(requestCode);
        if (weakReference == null) {
            weakReference = defCallBack;
        }
        if (weakReference != null) {
            listener = weakReference.get();
        }
        if (listener != null) {
            listener.onPermissionChecked(true, requestCode, permissions, missPermissionList);
        }
    }

    private void callback(IPermissionResultListener listener, boolean formResult, int requestCode, String[] requestPermissions, @Nullable List<String> missPermissions) {
        if (listener == null && defCallBack != null) {
            listener = defCallBack.get();
        }
        if (listener != null) {
            listener.onPermissionChecked(formResult, requestCode, requestPermissions, missPermissions);
        }
    }

    public boolean checkWriteSetting(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.System.canWrite(context)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return false;
            }
        }
        return true;
    }

    public boolean checkDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(context)) {
                Intent serviceIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + context.getPackageName()));
                context.startActivity(serviceIntent);
                return false;
            }
        }
        return true;
    }

    public boolean checkNoticeEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= 24) {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(
                    Context.NOTIFICATION_SERVICE);
            return mNotificationManager != null && mNotificationManager.areNotificationsEnabled();
        } else if (Build.VERSION.SDK_INT >= 19) {
            AppOpsManager appOps =
                    (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = context.getApplicationInfo();
            String pkg = context.getApplicationContext().getPackageName();
            int uid = appInfo.uid;
            try {
                Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE,
                        Integer.TYPE, String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
                int value = (int) opPostNotificationValue.get(Integer.class);
                return ((int) checkOpNoThrowMethod.invoke(appOps, value, uid, pkg)
                        == AppOpsManager.MODE_ALLOWED);
            } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException
                    | InvocationTargetException | IllegalAccessException | RuntimeException e) {
                return true;
            }
        } else {
            return true;
        }
    }

    //麦克风权限
    public boolean checkVoiceEnable() {
        try {
            AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC, 22050,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    AudioRecord.getMinBufferSize(22050, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                            AudioFormat.ENCODING_PCM_16BIT));
            record.startRecording();
            int recordingState = record.getRecordingState();
            if (recordingState == AudioRecord.RECORDSTATE_STOPPED) {
                return false;
            }
            record.release();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}