package com.zpf.update.test;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class VersionFileUtil {
    @NonNull
    private VersionInfo localVersion;
    private VersionInfo remoteVersion = null;
    private final INetCall requester;
    private final SharedPreferences preference;
    private final String cacheName;

    public VersionFileUtil(Context context, String name, INetCall requester) {
        this(context.getApplicationContext().getSharedPreferences("LOCAL_VERSION_RECORD_INFO", Context.MODE_PRIVATE), name, requester);
    }

    public VersionFileUtil(SharedPreferences preference, String name, INetCall requester) {
        this.preference = preference;
        this.requester = requester;
        this.cacheName = name;
        localVersion = new VersionInfo(preference.getString(name, ""));
    }

    @Nullable
    public File getLatestFile() {
        final VersionInfo localCache = localVersion;
        VersionInfo remoteCache = remoteVersion;
        if (remoteCache == null || remoteCache.version <= 0) {
            remoteVersion = requester.loadRemoteInfo(localCache.version);
            remoteCache = remoteVersion;
        }
        if (remoteCache == null || remoteCache.version == 0) {
            return null;
//            if (localCache == null || localCache.version == 0) {
//                //error
//                return null;
//            } else {
//                if (isFileUsable(localCache)) {
//                    //使用本地
//                    return new File(localCache.path);
//                } else {
//                    //error
//                    return null;
//                }
//            }
        } else {
            if (!isFileUsable(localCache) || remoteCache.version > localCache.version) {
                File file = requester.download(remoteCache.path);
                if (isFileUsable(file, remoteCache.md5Str)) {
                    //更新本地记录
                    localVersion = new VersionInfo(remoteCache.version, file.getAbsolutePath(), remoteCache.md5Str);
                    preference.edit().putString(cacheName, localVersion.toString()).commit();
                    return file;
                } else {
                    //error
                    return null;
                }
            } else {
                //使用本地
                return new File(localCache.path);
            }
        }
    }

    public boolean isFileUsable(VersionInfo info) {
        if (info == null || info.version <= 0 || info.path.isEmpty()) {
            return false;
        }
        return isFileUsable(new File(info.path), info.md5Str);
    }

    public boolean isFileUsable(File file, String md5Str) {
        if (file == null || !file.exists() || file.length() < 1) {
            return false;
        }
        if (md5Str.isEmpty()) {
            return true;
        }
        return md5Str.equals(getFileMD5(file));
    }

    public static @Nullable String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest;
        FileInputStream in;
        byte[] buffer = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            return null;
        }
        return bytesToHexString(digest.digest());
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length == 0) {
            return null;
        }
        for (byte b : src) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static class VersionInfo {
        public final int version;
        public final String path;
        public final String md5Str;

        public VersionInfo(int version, String path, String md5Str) {
            this.version = version;
            this.path = path;
            this.md5Str = md5Str;
        }

        public VersionInfo(String jsonString) {
            JSONObject versionInfo = null;
            try {
                versionInfo = new JSONObject(jsonString);
            } catch (Exception e) {
                //
            }
            if (versionInfo == null) {
                version = 0;
                md5Str = "";
                path = "";
            } else {
                version = versionInfo.optInt("version");
                md5Str = versionInfo.optString("md5Str");
                path = versionInfo.optString("path");
            }
        }

        @NonNull
        @Override
        public String toString() {
            JSONObject versionInfo = new JSONObject();
            try {
                versionInfo.putOpt("version", version);
                versionInfo.putOpt("md5Str", md5Str);
                versionInfo.putOpt("path", path);
            } catch (JSONException e) {
                //
            }
            return versionInfo.toString();
        }
    }

    public interface INetCall {

        VersionInfo loadRemoteInfo(int localVersion);

        File download(String url);
    }
}
