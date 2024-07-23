package com.zpf.file;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * @author Created by ZPF on 2021/5/31.
 */
public class FileSaveUtil {

    public static Uri saveFile(Context context, File srcFile, String displayName, String mimeType, boolean saveToDownload) {
        InputStream inputStream;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                inputStream = Files.newInputStream(srcFile.toPath());
            } else {
                inputStream = new FileInputStream(srcFile);
            }
        } catch (Exception e) {
            return null;
        }
        if (mimeType == null) {
            mimeType = FileTypeUtil.getFileMimeType(srcFile);
        }
        if (displayName == null || displayName.isEmpty()) {
            displayName = srcFile.getName();
        }
        return saveFile(context, inputStream, displayName, mimeType, saveToDownload);
    }

    public static Uri saveFile(Context context, Uri fileUri, String displayName, String mimeType, boolean saveToDownload) {
        ContentResolver resolver = context.getContentResolver();
        InputStream inputStream;
        try {
            inputStream = resolver.openInputStream(fileUri);
        } catch (Exception e) {
            return null;
        }
        if (mimeType == null) {
            mimeType = FileTypeUtil.getFileMimeType(context, fileUri);
        }
        return saveFile(context, inputStream, displayName, mimeType, saveToDownload);
    }

    public static Uri saveFile(Context context, InputStream inputStream, String displayName, String mimeType, boolean saveToDownload) {
        if (inputStream == null || context == null) {
            return null;
        }
        if (mimeType == null) {
            mimeType = "*/*";
        }
        if (displayName == null) {
            displayName = "Media_" + System.currentTimeMillis();
            String[] types = mimeType.split("/");
            String suffix = null;
            if (types.length > 1) {
                suffix = mimeType.split("/")[1];
            }
            if (suffix != null && suffix.length() > 1) {
                displayName = displayName + "." + suffix;
            }
        }
        ContentResolver resolver = context.getContentResolver();
        Uri uri = FileUriUtil.createMediaUri(resolver, displayName, mimeType, true, saveToDownload);
        if (uri == null) {
            uri = FileUriUtil.createMediaUri(resolver, displayName, mimeType, false, saveToDownload);
        }
        if (uri == null) {
            return null;
        }
        OutputStream outputStream = null;
        try {
            outputStream = resolver.openOutputStream(uri);
        } catch (Exception e) {
            //
        }
        if (!FileIOUtil.writeStream(inputStream, outputStream)) {
            resolver.delete(uri, null, null);
            return null;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.IS_PENDING, 0);
            resolver.update(uri, values, null, null);
        }
        return uri;
    }

    public static boolean saveBitmap(Bitmap bitmap, Bitmap.CompressFormat format, File destFile) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(destFile);
            Bitmap.CompressFormat compressFormat;
            if (format == null) {
                compressFormat = Bitmap.CompressFormat.PNG;
            } else {
                compressFormat = format;
            }
            if (bitmap.compress(compressFormat, 100, out)) {
                out.flush();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileIOUtil.quickClose(out);
        }
        return false;
    }

    public static boolean saveBitmap(Bitmap bitmap, Bitmap.CompressFormat format, Context context, Uri destUri) {
        OutputStream out = null;
        try {
            out = context.getContentResolver().openOutputStream(destUri);
            if (out == null) {
                return false;
            }
            Bitmap.CompressFormat compressFormat;
            if (format == null) {
                compressFormat = Bitmap.CompressFormat.PNG;
            } else {
                compressFormat = format;
            }
            if (bitmap.compress(compressFormat, 100, out)) {
                out.flush();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileIOUtil.quickClose(out);
        }
        return false;
    }
}