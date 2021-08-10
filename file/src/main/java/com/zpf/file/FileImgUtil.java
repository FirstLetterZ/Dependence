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
public class FileImgUtil {
    public static Uri saveImgToAlbum(Context context, String parentFolder, String displayName, Uri fileUri) {
        if (fileUri == null || context == null) {
            return null;
        }
        ContentResolver resolver = context.getContentResolver();
        String type = FileTypeUtil.getFileMimeType(context, fileUri);
        if (type == null) {
            type = "image/*";
        }
        if (displayName == null) {
            displayName = "IMAGE_" + System.currentTimeMillis();
        }
        if (!displayName.contains(".")) {
            String[] ts = type.split(File.separator);
            if (ts != null && ts.length > 1 && ts[1].length() > 1) {
                displayName = displayName + "." + ts[1];
            }
        }
        String addParentPath = "";
        if (parentFolder != null) {
            if (parentFolder.startsWith(File.separator)) {
                addParentPath = parentFolder;
            } else {
                addParentPath = File.separator + parentFolder;
            }
        }
        Uri uri = insertImageMedia(resolver, addParentPath, displayName, type);
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

    public static Uri saveImgToAlbum(Context context, String parentFolder, String displayName, File srcFile) {
        if (srcFile == null || context == null) {
            return null;
        }
        String type = FileTypeUtil.getFileMimeType(srcFile);
        if (type == null) {
            type = "image/*";
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
        String addParentPath = "";
        if (parentFolder != null) {
            if (parentFolder.startsWith(File.separator)) {
                addParentPath = parentFolder;
            } else {
                addParentPath = File.separator + parentFolder;
            }
        }
        ContentResolver resolver = context.getContentResolver();
        Uri uri = insertImageMedia(resolver, addParentPath, displayName, type);
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

    private static Uri insertImageMedia(ContentResolver resolver, String addParentPath, String fileName, String mimeType) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        values.put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + addParentPath);
        } else {
            String saveFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                    + addParentPath + File.separator + fileName;
            values.put(MediaStore.MediaColumns.DATA, saveFilePath);
        }
        return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
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
