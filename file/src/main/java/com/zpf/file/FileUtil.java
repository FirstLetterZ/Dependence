package com.zpf.file;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import androidx.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileUtil {

    public static Uri saveImgToAlbum(Context context, String parentFolder, String displayName, Uri fileUri) {
        if (fileUri == null || context == null) {
            return null;
        }
        ContentResolver resolver = context.getContentResolver();
        String type = getFileMimeType(resolver, fileUri);
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
        String type = getFileMimeType(srcFile);
        if (type == null) {
            type = "image/*";
        }
        if (displayName == null) {
            displayName = srcFile.getName();
        }
        if (!displayName.contains(".")) {
            String suffix = getSuffixName(srcFile.getAbsolutePath());
            if (suffix != null && suffix.length() > 0) {
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

    public static String getSuffixName(String filePath) {
        if (filePath == null) {
            return "";
        }
        String suffix = null;
        int i = filePath.lastIndexOf(".");
        if (i > 0) {
            try {
                suffix = filePath.substring(i + 1);
            } catch (Exception e) {
                //
            }
        }
        if (suffix == null) {
            return "";
        }
        return suffix;
    }

    public static void openFile(Context context, Uri fileUri, String openType) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (openType == null || openType.length() == 0) {
            intent.setDataAndType(fileUri, "*/*");
        } else {
            intent.setDataAndType(fileUri, openType);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }

    public static void installApk(Context context, Uri apkUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    //通知相册刷新
    public static void notifyPhotoAlbum(Context context, Uri fileUri) {
        if (context == null || fileUri == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(fileUri);
        context.sendBroadcast(intent);
    }

    @Nullable
    public static String getFileMimeType(ContentResolver resolver, Uri fileUri) {
        if (resolver == null || fileUri == null) {
            return null;
        }
        String mimeType = null;
        try {
            mimeType = resolver.getType(fileUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mimeType == null) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            try {
                ParcelFileDescriptor pfd = resolver.openFileDescriptor(fileUri, "r");
                mmr.setDataSource(pfd.getFileDescriptor());
                mimeType = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mimeType;
    }

    @Nullable
    public static String getFileMimeType(File file) {
        if (file == null || !file.isFile()) {
            return null;
        }
        String filePath = file.getAbsolutePath();
        String suffix = getSuffixName(filePath);
        String mimeType = null;
        if (suffix != null) {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
        }
        if (mimeType == null) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            try {
                mmr.setDataSource(filePath);
                mimeType = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mimeType;
    }

    public static File zipFiles(File[] files, String filename) throws IOException {
        File target;
        File zip = new File(filename);
        if (!zip.exists()) {
            if (zip.isDirectory()) {
                zip.mkdirs();
            } else {
                zip.createNewFile();
            }
        }
        if (!zip.exists()) {
            return null;
        } else {
            if (zip.isDirectory()) {
                target = new File(zip, "Files_" + System.currentTimeMillis() + ".zip");
                if (!target.exists()) {
                    target.createNewFile();
                }
            } else {
                target = zip;
            }
        }
        if (!target.exists()) {
            return null;
        }
        List<String> names = new ArrayList<>();
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(target));
        for (File f : files) {
            String name = f.getName();
            if (names.contains(name)) {
                name = UUID.randomUUID() + "_" + name;
            }
            zos.putNextEntry(new ZipEntry(name));
            names.add(name);
            InputStream fis = new FileInputStream(f);
            byte[] contentBytes = new byte[1024];
            int dataLen;
            while ((dataLen = fis.read(contentBytes)) > 0) {
                zos.write(contentBytes, 0, dataLen);
            }
            fis.close();
        }
        zos.closeEntry();
        zos.flush();
        zos.close();
        return target;
    }

    public static boolean upZipFile(String filePath, String folderPath) {
        ZipFile zipFile;
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        try {
            // 转码为GBK格式，支持中文
            zipFile = new ZipFile(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        Enumeration<?> zList = zipFile.entries();
        ZipEntry ze;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            // 列举的压缩文件里面的各个文件，判断是否为目录
            if (ze.isDirectory()) {
                String dirstr = folderPath + ze.getName();
                dirstr.trim();
                File f = new File(dirstr);
                f.mkdir();
                continue;
            }
            OutputStream os;
            FileOutputStream fos;
            // ze.getName()会返回 script/start.script这样的，是为了返回实体的File
            File realFile = getRealFileName(folderPath, ze.getName());
            try {
                fos = new FileOutputStream(realFile);
            } catch (FileNotFoundException e) {
                return false;
            }
            os = new BufferedOutputStream(fos);
            InputStream is;
            try {
                is = new BufferedInputStream(zipFile.getInputStream(ze));
            } catch (IOException e) {
                return false;
            }
            int readLen = 0;
            // 进行一些内容复制操作
            try {
                while ((readLen = is.read(buf, 0, 1024)) != -1) {
                    os.write(buf, 0, readLen);
                }
            } catch (IOException e) {
                return false;
            }
            try {
                is.close();
                os.close();
            } catch (IOException e) {
                return false;
            }
        }
        try {
            zipFile.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static File getRealFileName(String baseDir, String absFileName) {
        absFileName = absFileName.replace("\\", "/");
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                ret = new File(ret, substr);
            }

            if (!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length - 1];
            ret = new File(ret, substr);
            return ret;
        } else {
            ret = new File(ret, absFileName);
        }
        return ret;
    }

    public static File getFileOrCreate(String folderPath, String name) {
        File file = new File(folderPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File f = new File(file, name);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return f;
    }

    public static boolean delete(File file) {
        if (!file.exists()) {
            return true;
        }
        if (file.isDirectory()) {
            File[] ff = file.listFiles();
            if (ff != null && ff.length > 0) {
                for (File f : file.listFiles()) {
                    delete(f);
                }
            }
        }
        try {
            return file.delete();
        } catch (Exception e) {
            return false;
        }
    }
}
