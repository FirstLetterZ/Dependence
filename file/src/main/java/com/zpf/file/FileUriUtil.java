package com.zpf.file;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.lang.reflect.Method;

/**
 * @author Created by ZPF on 2021/5/28.
 */
public class FileUriUtil {
    public static final String PROVIDER_NAME = "androidx.core.content.FileProvider";
    public static final String PROVIDER_SUFFIX = "FileProvider";

    //should add "-keep public class androidx.core.content.FileProvider{*;}" in "proguard-rules.pro"
    public static Uri getFileUri(@NonNull Context context, @NonNull File file) {
        if (Build.VERSION.SDK_INT <= 19) {
            return Uri.fromFile(file);
        }
        Uri fileUri = null;
        ProviderInfo providerAuthority = getFileProvider(context);
        if (providerAuthority != null) {
            try {
                Class<?> provider = Class.forName(providerAuthority.name);
                Method method = provider.getMethod("getUriForFile", Context.class, String.class, File.class);
                fileUri = (Uri) method.invoke(null, context, providerAuthority.authority, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (fileUri != null) {
            return fileUri;
        }
        ContentResolver resolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
        fileUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        if (fileUri == null) {
            fileUri = resolver.insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, contentValues);
        }
        return fileUri;
    }

    public static ProviderInfo getFileProvider(@NonNull Context context) {
        return getFileProvider(context, PROVIDER_NAME, PROVIDER_SUFFIX);
    }

    public static ProviderInfo getFileProvider(@NonNull Context context, @Nullable String name, @Nullable String suffix) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_PROVIDERS);
            ProviderInfo[] providers = packageInfo.providers;
            ProviderInfo matchName = null;
            ProviderInfo matchSuffix = null;
            for (ProviderInfo providerInfo : providers) {
                if (providerInfo.name != null) {
                    if (providerInfo.name.equals(name)) {
                        matchName = providerInfo;
                        break;
                    }
                    if (suffix != null && suffix.length() > 0 && providerInfo.name.endsWith(suffix)) {
                        matchSuffix = providerInfo;
                    }
                }
            }
            if (matchName != null) {
                return matchName;
            }
            if (matchSuffix != null) {
                return matchSuffix;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Uri createMediaUri(ContentResolver resolver, String fileName, String mimeType, boolean external, boolean saveToDownload) {
        String saveDirectory = Environment.DIRECTORY_DOWNLOADS;
        Uri insertUri;
        if (mimeType == null) {
            mimeType = "*/*";
        }
        if (saveToDownload) {
            insertUri = getFileDownloadUri(external);
        } else {
            String lowercaseType = mimeType.toLowerCase();
            if (lowercaseType.startsWith("image")) {
                saveDirectory = Environment.DIRECTORY_PICTURES;
                if (external) {
                    insertUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else {
                    insertUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
                }
            } else if (lowercaseType.startsWith("video")) {
                saveDirectory = Environment.DIRECTORY_MOVIES;
                if (external) {
                    insertUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else {
                    insertUri = MediaStore.Video.Media.INTERNAL_CONTENT_URI;
                }
            } else if (lowercaseType.startsWith("audio")) {
                saveDirectory = Environment.DIRECTORY_MUSIC;
                if (external) {
                    insertUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                } else {
                    insertUri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
                }
            } else {
                insertUri = getFileDownloadUri(external);
            }
        }
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, saveDirectory);
        } else {
            String saveFilePath = Environment.getExternalStoragePublicDirectory(saveDirectory).getAbsolutePath() + File.separator + fileName;
            File pf = new File(saveFilePath).getParentFile();
            if (pf != null && !pf.exists()) {
                pf.mkdirs();
            }
            values.put(MediaStore.MediaColumns.DATA, saveFilePath);
        }
        try {
            return resolver.insert(insertUri, values);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Uri getFileDownloadUri(boolean external) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (external) {
                return MediaStore.Downloads.EXTERNAL_CONTENT_URI;
            } else {
                return MediaStore.Downloads.INTERNAL_CONTENT_URI;
            }
        } else {//todo zpf
//            "content://downloads/public_downloads"
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                if (external) {
                    return MediaStore.Downloads.EXTERNAL_CONTENT_URI;
                } else {
                    return MediaStore.Downloads.INTERNAL_CONTENT_URI;
                }
            }
            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            return Uri.fromFile(downloadDir);
        }
    }

    /**
     * 注意 Build.VERSION_CODES.Q 版本下获得的路径，非包名下的路径不可直接使用
     */
    public static String uriToPath(@NonNull Context context, @NonNull Uri fileUri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, fileUri)) {
            if (isExternalStorageDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            } else if (isDownloadsDocument(fileUri)) {
                String id = DocumentsContract.getDocumentId(fileUri);
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.parseLong(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(fileUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(fileUri)) {
                return fileUri.getLastPathSegment();
            }
            return getDataColumn(context, fileUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(fileUri.getScheme())) {
            return fileUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }
}