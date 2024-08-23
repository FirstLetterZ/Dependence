package com.zpf.file;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CompressUtil {
    //用于兼容Android Q，以及仅能通过uri读取文件流的情况
    public static int compress(InputStream sourceStream, String targetFilePath, int outWidth, int outHeight, int quality) {
        return compress(null, sourceStream, targetFilePath, outWidth, outHeight, quality);
    }

    public static int compress(String sourceFilePath, String targetFilePath, int outWidth, int outHeight, int quality) {
        return compress(sourceFilePath, null, targetFilePath, outWidth, outHeight, quality);
    }

    private static int compress(String sourceFilePath, InputStream sourceStream, String targetFilePath,
                                int outWidth, int outHeight, int quality) {
        if (sourceFilePath != null) {
            try {
                sourceStream = new FileInputStream(sourceFilePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (sourceStream == null || targetFilePath == null || quality < 0 || quality > 100) {
            safeClose(sourceStream);
            return CompressErrorCode.ERROR_CHECK_OPTION;
        }
        try {
            if (!(sourceStream instanceof BufferedInputStream)) {
                sourceStream = new BufferedInputStream(sourceStream);
            }
            sourceStream.mark(sourceStream.available());
        } catch (Exception e) {
            return CompressErrorCode.ERROR_CHECK_OPTION;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;
        try {
            BitmapFactory.decodeStream(sourceStream, null, options);
        } catch (Exception e) {
            safeClose(sourceStream);
            return CompressErrorCode.ERROR_READ_FILE;
        }
        options.inJustDecodeBounds = false;
        int originalWidth = options.outWidth;
        int originalHeight = options.outHeight;
        if (originalWidth <= 0 || originalHeight <= 0) {
            safeClose(sourceStream);
            return CompressErrorCode.ERROR_CHECK_OPTION;
        }
        if (outHeight <= 0 && outWidth <= 0) {
            options.inSampleSize = computeSize(originalWidth, originalHeight);
        } else {
            if (outHeight <= 0) {
                outHeight = originalHeight;
            }
            if (outWidth <= 0) {
                outWidth = originalWidth;
            }
            options.inSampleSize = computeScale(outWidth, outHeight, originalWidth, originalHeight);
        }
        Bitmap sourceBitmap = null;
        try {
            sourceStream.reset();
            sourceBitmap = BitmapFactory.decodeStream(sourceStream, null, options);
        } catch (Exception e) {
            //
        }
        if (sourceBitmap == null) {
            safeClose(sourceStream);
            return CompressErrorCode.ERROR_READ_FILE;
        }
        int picDegree = 0;
        if (sourceFilePath != null) {
            picDegree = readPictureDegree(sourceFilePath);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            picDegree = readPictureDegree(sourceStream);
        }
        if (picDegree != 0) {
            sourceBitmap = rotatingImage(sourceBitmap, picDegree);
        }
        File targetFile = new File(targetFilePath);
        File targetParent = targetFile.getParentFile();
        if (targetParent != null && !targetParent.exists()) {
            targetParent.mkdirs();
        }
        Bitmap.CompressFormat format = null;
        if (options.outMimeType != null) {
            if (options.outMimeType.endsWith("png")) {
                format = Bitmap.CompressFormat.PNG;
            } else if (options.outMimeType.endsWith("webp")) {
                format = Bitmap.CompressFormat.WEBP;
            } else if (options.outMimeType.endsWith("jpg") || options.outMimeType.endsWith("jpeg")) {
                format = Bitmap.CompressFormat.JPEG;
            }
        }
        if (format == null && sourceFilePath != null) {
            try {
                sourceStream.reset();
                int typeCode = FileTypeUtil.getFileTypeCode(sourceStream);
                if (FileType.PNG == typeCode) {
                    format = Bitmap.CompressFormat.PNG;
                } else if (FileType.WEBP == typeCode) {
                    format = Bitmap.CompressFormat.WEBP;
                } else {
                    format = Bitmap.CompressFormat.JPEG;
                }
            } catch (IOException ignore) {
                //
            }
        }
        if (format == null) {
            format = Bitmap.CompressFormat.JPEG;
        }
        if (format != Bitmap.CompressFormat.PNG) {
            options.inPreferredConfig = Bitmap.Config.RGB_565;
        }
        try (FileOutputStream fos = new FileOutputStream(targetFilePath)) {
            sourceBitmap.compress(format, quality, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return CompressErrorCode.ERROR_WHITE_FILE;
        } finally {
            safeClose(sourceStream);
            sourceBitmap.recycle();
        }
        return CompressErrorCode.SUCCESS_ANDROID;
    }

    public static int readPictureDegree(InputStream inputStream) {
        int degree = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                ExifInterface exifInterface = new ExifInterface(inputStream);
                degree = getDegree(exifInterface);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return degree;
    }

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            degree = getDegree(exifInterface);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static int getDegree(ExifInterface exifInterface) {
        int degree = 0;
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
        }
        return degree;
    }

    private static void safeClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                //
            }
        }
    }

    public static int computeScale(int targetWidth, int targetHeight, int originalWidth, int originalHeight) {
        if (targetHeight <= 0 || targetWidth <= 0 || originalWidth <= 0 || originalHeight <= 0) {
            return 1;
        }
        if (targetHeight >= originalHeight && targetWidth >= originalWidth) {
            return 1;
        }
        int scale = 1;
        float diffWidth = Math.abs(targetWidth - originalWidth) * 1f / targetWidth;
        float diffHeight = Math.abs(targetHeight - originalHeight) * 1f / targetHeight;
        if (diffHeight <= 0.01f && diffWidth <= 0.01f) {
            return scale;
        }
        float diffSum = diffWidth + diffHeight;
        int tempScale;
        float lastDiffSum;
        while (true) {
            tempScale = scale * 2;
            lastDiffSum = diffSum;
            diffWidth = Math.abs(targetWidth * tempScale - originalWidth) * 1f / targetWidth;
            diffHeight = Math.abs(targetHeight * tempScale - originalHeight) * 1f / targetHeight;
            if (diffHeight < 0.01f && diffWidth <= 0.01f) {
                return tempScale;
            }
            diffSum = diffWidth + diffHeight;
            if (diffSum > lastDiffSum) {
                return scale;
            }
            scale = tempScale;
        }
    }

    //使用了开源库luban的算法
    public static int computeSize(int width, int height) {
        width = width % 2 == 1 ? width + 1 : width;
        height = height % 2 == 1 ? height + 1 : height;
        int longSide = Math.max(width, height);
        int shortSide = Math.min(width, height);
        float scale = ((float) shortSide / longSide);
        if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                return 1;
            } else if (longSide < 4990) {
                return 2;
            } else if (longSide > 4990 && longSide < 10240) {
                return 4;
            } else {
                return longSide / 1280;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            return longSide / 1280 == 0 ? 1 : longSide / 1280;
        } else {
            return (int) Math.ceil(longSide / (1280.0 / scale));
        }
    }

    public static Bitmap rotatingImage(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

}