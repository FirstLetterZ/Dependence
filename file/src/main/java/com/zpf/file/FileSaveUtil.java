package com.zpf.file;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Created by ZPF on 2021/5/31.
 */
public class FileSaveUtil {
    public static Uri saveFile(Context context, File srcFile, String displayName, String mimeType) {
        if (srcFile == null || context == null || !srcFile.exists()) {
            return null;
        }
        if (mimeType == null) {
            mimeType = FileTypeUtil.getFileMimeType(srcFile);
        }
        if (displayName == null) {
            displayName = srcFile.getName();
        }
        if (!displayName.contains(".")) {
            String suffix = FileUtil.getSuffixName(srcFile.getAbsolutePath());
            if (suffix.length() > 0) {
                displayName = displayName + "." + suffix;
            }
        }
        ContentResolver resolver = context.getContentResolver();
        Uri uri = insertImageMedia(resolver, displayName, mimeType);
        if (uri == null) {
            return null;
        }
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            outputStream = resolver.openOutputStream(uri);
            inputStream = new FileInputStream(srcFile);
        } catch (Exception e) {
            //
        }
        if (!FileIOUtil.writeStream(inputStream, outputStream)) {
            resolver.delete(uri, null, null);
            return null;
        }
        return uri;
    }

    public static Uri saveUri(Context context, Uri fileUri, String displayName, String mimeType) {
        if (fileUri == null || context == null) {
            return null;
        }
        if (mimeType == null) {
            mimeType = FileTypeUtil.getFileMimeType(context, fileUri);
        }
        if (displayName == null) {
            displayName = "Media_" + System.currentTimeMillis();
        }
        if (!displayName.contains(".") && mimeType != null) {
            String[] ts = mimeType.split(File.separator);
            if (ts.length > 1 && ts[1].length() > 1) {
                displayName = displayName + "." + ts[1];
            }
        }
        ContentResolver resolver = context.getContentResolver();
        Uri uri = insertImageMedia(resolver, displayName, mimeType);
        if (uri == null) {
            return null;
        }
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            outputStream = resolver.openOutputStream(uri);
            inputStream = resolver.openInputStream(fileUri);
        } catch (Exception e) {
            //
        }
        if (!FileIOUtil.writeStream(inputStream, outputStream)) {
            resolver.delete(uri, null, null);
            return null;
        }
        return uri;
    }

    private static Uri insertImageMedia(ContentResolver resolver, String fileName, String mimeType) {
        String saveDirectory = Environment.DIRECTORY_DOWNLOADS;
        Uri insertUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            insertUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        } else {
            insertUri = Uri.parse("content://downloads/public_downloads");
        }
        if (mimeType != null) {
            String lowercaseType = mimeType.toLowerCase();
            if (lowercaseType.startsWith("image")) {
                saveDirectory = Environment.DIRECTORY_PICTURES;
                insertUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if (lowercaseType.startsWith("video")) {
                saveDirectory = Environment.DIRECTORY_MOVIES;
                insertUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if (lowercaseType.startsWith("audio")) {
                saveDirectory = Environment.DIRECTORY_MUSIC;
                insertUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }
        } else {
            mimeType = "*/*";
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
        return resolver.insert(insertUri, values);
    }

    public static boolean saveBitmap(Bitmap bitmap, File destFile) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(destFile);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
            }
            return true;
        } catch (Exception e) {
            FileIOUtil.quickClose(out);
            e.printStackTrace();
        }
        return false;
    }
}
