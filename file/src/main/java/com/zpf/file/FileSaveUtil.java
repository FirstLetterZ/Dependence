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
        Uri uri = FileUriUtil.createMediaUri(resolver, displayName, mimeType);
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
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.IS_PENDING, 0);
            resolver.update(uri, values, null, null);
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
        Uri uri = FileUriUtil.createMediaUri(resolver, displayName, mimeType);
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
            FileIOUtil.quickClose(out);
            return true;
        } catch (Exception e) {
            FileIOUtil.quickClose(out);
            e.printStackTrace();
        }
        return false;
    }

    public static boolean saveBitmap(Bitmap bitmap, Bitmap.CompressFormat format, Context context, Uri destUri) {
        OutputStream out = null;
        try {
            out = context.getContentResolver().openOutputStream(destUri);
            Bitmap.CompressFormat compressFormat;
            if (format == null) {
                compressFormat = Bitmap.CompressFormat.PNG;
            } else {
                compressFormat = format;
            }
            if (bitmap.compress(compressFormat, 100, out)) {
                out.flush();
            }
            FileIOUtil.quickClose(out);
            return true;
        } catch (Exception e) {
            FileIOUtil.quickClose(out);
            e.printStackTrace();
        }
        return false;
    }
}
