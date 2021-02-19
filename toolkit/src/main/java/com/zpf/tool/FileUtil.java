package com.zpf.tool;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.zpf.tool.config.AppContext;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileUtil {

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
    public static void notifyPhotoAlbum(Context context, String filePath) {
        if (context == null || TextUtils.isEmpty(filePath)) {
            return;
        }
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = FileUtil.getUri(context, filePath);
                intent.setData(uri);
                context.sendBroadcast(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getProviderAuthority(Context context, String name) {
        String authority = "";
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_PROVIDERS);
            ProviderInfo[] providers = packageInfo.providers;
            for (ProviderInfo providerInfo : providers) {
                if (TextUtils.equals(providerInfo.name, name)) {
                    authority = providerInfo.authority;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return authority;
    }

    public static List<String> getProviderAuthorityList(Context context) {
        List<String> providerArray = new ArrayList<>();
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_PROVIDERS);
            ProviderInfo[] providers = info.providers;
            if (providers != null && providers.length > 0) {
                for (ProviderInfo provider : providers) {
                    providerArray.add(provider.authority);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return providerArray;
    }

    public static String getCameraCachePath() {
        if (TextUtils.equals(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState())) {
            if (!Environment.isExternalStorageRemovable()) {
                return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
            } else {
                return Environment.getExternalStorageDirectory().getAbsolutePath() + "/photo";
            }
        } else {
            return Environment.getDataDirectory().getAbsolutePath() + "/photo";
        }
    }

    public static String getDownloadCachePath() {
        if (TextUtils.equals(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState())) {
            if (!Environment.isExternalStorageRemovable()) {
                return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            } else {
                return Environment.getExternalStorageDirectory().getAbsolutePath() + "/download";
            }
        } else {
            return Environment.getDataDirectory().getAbsolutePath() + "/download";
        }
    }

    public static String getAppDataPath() {
        return getAppDataPath(AppContext.get());
    }

    public static String getAppDataPath(Context context) {
        File dataFileDir = context.getExternalFilesDir("dataFiles");
        if (dataFileDir != null && dataFileDir.exists()) {
            return dataFileDir.getAbsolutePath();
        } else {
            return context.getFilesDir().getAbsolutePath();
        }
    }

    public static String getAppCachePath() {
        return getAppDataPath(AppContext.get());
    }

    public static String getAppCachePath(Context context) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir != null && cacheDir.exists()) {
            return cacheDir.getAbsolutePath();
        } else {
            return context.getCacheDir().getAbsolutePath();
        }
    }

    //获取路径的uri
    public static Uri getUri(Context context, String path) {
        if (Build.VERSION.SDK_INT <= 19) {
            return Uri.fromFile(new File(path));
        }
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(MediaStore.Images.Media.DATA, path);
        return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
    }

    //向文件中写入数据
    public static boolean writeToFile(String file, String content) {
        return writeToFile(file, content, Charset.defaultCharset());
    }

    public static boolean writeToFile(String file, String content, Charset charset) {
        try {
            byte[] data = content.getBytes(charset);
            return writeToFile(file, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean writeToFile(String filePath, byte[] data) {
        BufferedOutputStream bufferedOut = null;
        try {
            bufferedOut = new BufferedOutputStream(new FileOutputStream(filePath));
            bufferedOut.write(data);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedOut != null) {
                    bufferedOut.flush();
                    bufferedOut.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean writeToFile(String filePath, InputStream inputStream) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            return writeToFile(inputStream, fos);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean writeToFile(InputStream inputStream, OutputStream outputStream) {
        byte[] buf = new byte[2048];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
                outputStream.flush();
            }
            return true;
        } catch (Exception e) {
            //
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                //
            }
            try {
                if (outputStream != null) outputStream.close();
            } catch (IOException e) {
                //
            }
        }
        return false;
    }

    public static boolean saveBitmapToFile(String filePath, Bitmap bitmap) {
        FileOutputStream out;
        try {
            out = new FileOutputStream(filePath);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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

    public static byte[] readBytes(String file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            int len = fis.available();
            byte[] buffer = new byte[len];
            fis.read(buffer);
            fis.close();
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readFile(String fileName) {
        return readFile(fileName, "UTF-8");
    }

    public static String readFile(String fileName, String charset) {
        byte[] data = readBytes(fileName);
        if (data == null || data.length == 0) {
            return null;
        } else {
            try {
                return new String(data, charset);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static String readAssetFile(Context context, String fileName) {
        try (InputStream in = context.getAssets().open(fileName)) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line;
            StringBuilder sb = new StringBuilder();
            do {
                line = bufferedReader.readLine();
                if (line != null && !line.matches("^\\s*\\/\\/.*")) {
                    sb.append(line);
                }
            } while (line != null);

            bufferedReader.close();
            in.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPropertyValue(Context context, String fileName) {
        String name = loadProperties(context, fileName).getProperty("NAME");
        String result = null;
        try {
            result = new String(name.getBytes(StandardCharsets.ISO_8859_1), "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Properties loadProperties(Context context, String fileName) {
        Properties properties = new Properties();
        try {
            InputStream in = context.getAssets().open(fileName);
            properties.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    /**
     * 通过URI取得文件绝对路径
     */
    public static String getPath(Context context, Uri imageUri) {
        if (context == null || imageUri == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.parseLong(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
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
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri)) {
                return imageUri.getLastPathSegment();
            }
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
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
