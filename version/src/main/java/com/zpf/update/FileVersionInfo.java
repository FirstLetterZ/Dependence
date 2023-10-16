package com.zpf.update;

import org.json.JSONObject;

/**
 * @author Created by ZPF on 2021/3/29.
 */
public class FileVersionInfo {
    String fileName;
    String fileIcon;
    int versionCode;
    String versionName;
    String downloadPath;
    String localPath;
    String md5Str;
    int strategy;//1--后台下载；2--提示下载；3--
    long checkTime;

    public FileVersionInfo() {
    }

    public FileVersionInfo(String name, String jsonString) {
        try {
            JSONObject versionInfo = new JSONObject(jsonString);
            fileName = versionInfo.optString("fileName");
            versionCode = versionInfo.optInt("versionCode");
            versionName = versionInfo.optString("versionName");
            md5Str = versionInfo.optString("md5Str");
            downloadPath = versionInfo.optString("downloadPath");
            localPath = versionInfo.optString("localPath");
            fileIcon = versionInfo.optString("fileIcon");
            strategy = versionInfo.optInt("strategy");
            checkTime = versionInfo.optLong("checkTime");
        } catch (Exception e) {
            //
        }
        if (fileName == null || fileName.length() == 0) {
            fileName = name;
        }
    }

    public String getFileName() {
        return fileName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public String getLocalPath() {
        return localPath;
    }

    public String getFileIcon() {
        return fileIcon;
    }

    public String getMd5Str() {
        return md5Str;
    }

    public int getStrategy() {
        return strategy;
    }

    @Override
    public String toString() {
        return "{" +
                "fileName='" + fileName + '\'' +
                ", fileIcon='" + fileIcon + '\'' +
                ", versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                ", downloadPath='" + downloadPath + '\'' +
                ", localPath='" + localPath + '\'' +
                ", md5Str='" + md5Str + '\'' +
                ", strategy=" + strategy +
                ", checkTime=" + checkTime +
                '}';
    }
}