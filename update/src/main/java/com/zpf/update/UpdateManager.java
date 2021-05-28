package com.zpf.update;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Created by ZPF on 2021/3/29.
 */
public class UpdateManager {
    public static final String RECORD_NAME = "file_version_record";
    private final SharedPreferences preference;
    private INetCall netCall;
    private long validTime = 3600000;//缓存有效时间，默认一小时
    private boolean autoUpZip = false;//自动解压
    private final Context appContext;

    private static volatile UpdateManager updateManager;

    public static UpdateManager get(Context context) {
        if (updateManager == null) {
            synchronized (UpdateManager.class) {
                if (updateManager == null) {
                    updateManager = new UpdateManager(context);
                }
            }
        }
        return updateManager;
    }

    public UpdateManager(@NonNull Context context) {
        appContext = context.getApplicationContext();
        preference = appContext.getSharedPreferences(RECORD_NAME, Context.MODE_PRIVATE);
    }

    //远端版本信息
    private final ConcurrentHashMap<String, FileVersionInfo> versionCache = new ConcurrentHashMap<>();
    //本地版本信息
    private final ConcurrentHashMap<String, FileVersionInfo> versionLocal = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, IUpdateListener> listenerMap = new ConcurrentHashMap<>();
    //版本升级接口监听
    private final INetResultListener checkVersionListener = new INetResultListener() {

        @Override
        public void onFail(String fileName, int code, String message) {
            final IUpdateListener loadListener = listenerMap.get(fileName);
            if (loadListener == null) {
                return;
            }
            FileVersionInfo localInfo = getLocalVersionInfo(fileName);
            if (easyCheckFileExists(localInfo.localPath)) {//尝试使用本地版本
                loadListener.onSuccess(localInfo);
                listenerMap.remove(localInfo.fileName);
            } else {
                loadListener.onFail(fileName, code, message);
            }
        }

        @Override
        public void onSuccess(FileVersionInfo versionInfo) {
            //更新远端版本信息
            versionInfo.checkTime = System.currentTimeMillis();
            versionCache.put(versionInfo.fileName, versionInfo);
            checkDownload(getLocalVersionInfo(versionInfo.fileName), versionInfo);
        }
    };
    //文件下载接口监听
    private final IDownloadListener downloadListener = new IDownloadListener() {

        @Override
        public void onChanged(String fileName, long total, long current) {
            final IUpdateListener loadListener = listenerMap.get(fileName);
            if (loadListener != null) {
                loadListener.onChanged(fileName, total, current);
            }
        }

        @Override
        public void onFail(String fileName, int code, String message) {
            final IUpdateListener loadListener = listenerMap.get(fileName);
            if (loadListener != null) {
                loadListener.onFail(fileName, code, message);
            }
        }

        @Override
        public void onSuccess(FileVersionInfo versionInfo) {
            final IUpdateListener loadListener = listenerMap.get(versionInfo.fileName);
            String baseFolderPath = UpdateUtil.getRootFolderPath(appContext, versionInfo.fileName);
            String versionFolderPath = baseFolderPath + File.separator + versionInfo.versionCode;
            if (versionInfo.localPath == null || versionInfo.localPath.length() == 0) {
                versionInfo.localPath = versionFolderPath + File.separator + versionInfo.fileName;
            }
            File resultFile = new File(versionInfo.localPath);
            if (versionInfo.md5Str == null || versionInfo.md5Str.length() == 0 || versionInfo.md5Str.equals(UpdateUtil.getFileMD5(resultFile))) {
                if (autoUpZip) {
                    if (!UpdateUtil.upZipFile(versionInfo.localPath, versionFolderPath)) {
                        if (loadListener != null) {
                            loadListener.onFail(versionInfo.fileName, -2, "文件解压失败");
                        }
                        return;
                    }
                }
                SharedPreferences.Editor editor = preference.edit();
                //更新旧版本信息
                FileVersionInfo oldVersionInfo = getLocalVersionInfo(versionInfo.fileName);
                if (easyCheckFileExists(oldVersionInfo.localPath)) {
                    editor.putString("old_" + versionInfo.fileName, oldVersionInfo.toString());
                }
                //更新本地版本信息
                versionLocal.put(versionInfo.fileName, versionInfo);
                editor.putString(versionInfo.fileName, versionInfo.toString()).commit();
                UpdateUtil.deleteOtherFolder(new File(baseFolderPath), Arrays.asList(baseFolderPath + File.separator + oldVersionInfo.versionCode,
                        baseFolderPath + File.separator + versionInfo.versionCode));
                if (loadListener != null) {
                    loadListener.onSuccess(versionInfo);
                    listenerMap.remove(versionInfo.fileName);
                }
            } else {
                if (loadListener != null) {
                    loadListener.onFail(versionInfo.fileName, -1, "下载内容已损坏");
                }
            }
        }
    };

    private void checkDownload(@NonNull final FileVersionInfo oldVersion, @NonNull FileVersionInfo newVersion) {
        boolean shouldDownload = newVersion.versionCode > oldVersion.versionCode;
        if (!easyCheckFileExists(oldVersion.localPath)) {
            //缺少本地文件，强制前台下载
            newVersion.strategy = 0;
            shouldDownload = true;
        }
        final String fileName = newVersion.fileName;
        final IUpdateListener loadListener = listenerMap.get(fileName);
        if (shouldDownload) {
            if (newVersion.strategy == 2) {//提示下载
                if (loadListener == null) {
                    return;
                }
                boolean alert = loadListener.alertUpdate(newVersion, new IAlertUpdateCallback() {
                    @Override
                    public void startDownload(boolean start) {
                        if (start) {
                            //前台下载
                            loadListener.onStart(fileName);
                        } else {
                            //直接加载本地版本
                            loadListener.onSuccess(oldVersion);
                            listenerMap.remove(fileName);
                        }
                    }
                });
                if (!alert) {
                    //前台下载
                    loadListener.onStart(fileName);
                    download(newVersion, netCall, loadListener);
                }
            } else {
                if (loadListener != null) {
                    if (newVersion.strategy == 1) {
                        loadListener.onSuccess(oldVersion);
                        listenerMap.remove(fileName);
                    } else {
                        loadListener.onStart(fileName);
                    }
                }
                download(newVersion, netCall, loadListener);
            }
        } else if (loadListener != null) {
            loadListener.onSuccess(oldVersion);
            listenerMap.remove(fileName);
        }
    }

    public synchronized void initLocalAsset(@NonNull String fileName, String assetPath, String fileIcon) {
        FileVersionInfo localVersion = getLocalVersionInfo(fileName);
        if (localVersion == null || localVersion.versionCode == 0) {
            String rootFolderPath = UpdateUtil.getRootFolderPath(appContext, fileName);
            File rootFolder = new File(rootFolderPath);
            boolean noLocal = !rootFolder.exists() || !rootFolder.isDirectory() || rootFolder.list().length == 0;
            boolean saveVersion;
            if (noLocal) {
                saveVersion = UpdateUtil.copyFromAsset(appContext, assetPath, rootFolderPath + File.separator + "0");
            } else {
                saveVersion = true;
            }
            if (saveVersion) {
                localVersion.versionCode = 0;
                localVersion.fileName = fileName;
                localVersion.fileIcon = fileIcon;
                localVersion.localPath = rootFolderPath + File.separator + "0";
                localVersion.versionName = "0.0.01";
                versionLocal.put(fileName, localVersion);
            }
        }
    }

    public synchronized void checkFileVersion(@NonNull String fileName) {
        final IUpdateListener loadListener = listenerMap.get(fileName);
        final INetCall call = netCall;
        if (call == null || fileName == null) {
            if (loadListener != null) {
                loadListener.onFail(fileName, -3, "未设置接口信息");
            }
            return;
        }
        FileVersionInfo localVersion = getLocalVersionInfo(fileName);
        FileVersionInfo cacheVersion = versionCache.get(fileName);
        boolean shouldCallNet = !easyCheckFileExists(localVersion.localPath);
        if (!shouldCallNet) {
            shouldCallNet = ((System.currentTimeMillis() - localVersion.checkTime > validTime)
                    && (cacheVersion == null || System.currentTimeMillis() - cacheVersion.checkTime > validTime));
        }
        if (shouldCallNet) {
            if (loadListener != null) {
                loadListener.onStart(fileName);
            }
            call.checkVersion(localVersion, checkVersionListener);
        } else if (cacheVersion != null) {
            checkDownload(localVersion, cacheVersion);
        } else {
            //使用本地版本
            if (loadListener != null) {
                loadListener.onSuccess(localVersion);
                listenerMap.remove(localVersion.fileName);
            }
        }
    }

    public void download(FileVersionInfo fileVersionInfo, INetCall call, IUpdateListener loadListener) {
        if (call == null || fileVersionInfo == null || fileVersionInfo.downloadPath == null) {
            if (loadListener != null) {
                loadListener.onFail(fileVersionInfo.fileName, -3, "未设置接口信息");
            }
            return;
        }
        File resultFile = UpdateUtil.getFileOrCreate(
                UpdateUtil.getRootFolderPath(appContext, fileVersionInfo.fileName)
                        + File.separator + fileVersionInfo.versionCode, fileVersionInfo.fileName);
        fileVersionInfo.localPath = resultFile.getAbsolutePath();
        call.download(fileVersionInfo, downloadListener);
    }

    @Nullable
    public FileVersionInfo getOldVersionInfo(String fileName) {
        String versionJson = preference.getString("old_" + fileName, null);
        FileVersionInfo oldInfo = new FileVersionInfo(fileName, versionJson);
        if (easyCheckFileExists(oldInfo.localPath)) {
            return oldInfo;
        }
        return null;
    }

    public boolean easyCheckFileExists(String filePath) {
        if (filePath == null || filePath.length() == 0) {
            return false;
        }
        File localFile = new File(filePath);
        return localFile.exists() || UpdateUtil.isNotEmptyDirectory(localFile.getParentFile());
    }

    @NonNull
    public String getLocalFilePath(String fileName) {
        FileVersionInfo localInfo = getLocalVersionInfo(fileName);
        return UpdateUtil.getRootFolderPath(appContext, fileName) + File.separator + localInfo.versionCode;
    }

    @NonNull
    public FileVersionInfo getLocalVersionInfo(String fileName) {
        FileVersionInfo localInfo = versionLocal.get(fileName);
        if (localInfo == null) {
            String versionJson = preference.getString(fileName, null);
            localInfo = new FileVersionInfo(fileName, versionJson);
            versionLocal.put(fileName, localInfo);
        }
        return localInfo;
    }

    public void setValidTime(long validTime) {
        if (validTime > 10000) {
            this.validTime = validTime;
        } else {
            this.validTime = 10000;
        }
    }

    public boolean isAutoUpZip() {
        return autoUpZip;
    }

    public void setAutoUpZip(boolean autoUpZip) {
        this.autoUpZip = autoUpZip;
    }

    public void setNetCall(INetCall netCall) {
        this.netCall = netCall;
    }

    public void setLoadListener(String fileName, IUpdateListener loadListener) {
        listenerMap.put(fileName, loadListener);
    }

    public void removeLoadListener(String fileName, IUpdateListener loadListener) {
        listenerMap.remove(fileName);
    }

    public void clearAllListener() {
        listenerMap.clear();
    }
}
