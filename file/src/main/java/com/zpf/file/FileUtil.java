package com.zpf.file;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileUtil {

    public static String makeFileName(String originalName, String prefix, String suffix, String algorithm) {
        StringBuilder builder = new StringBuilder();
        if (prefix != null) {
            builder.append(prefix);
        }
        String type = null;
        if (originalName != null) {
            int pointIndex = originalName.lastIndexOf(".");
            if (pointIndex > 0) {
                type = originalName.substring(pointIndex + 1);
                builder.append(originalName.substring(0, pointIndex));
            } else {
                builder.append(originalName);
            }
        }
        if (suffix != null) {
            builder.append(suffix);
        }
        String fileName = digest(builder.toString(), algorithm, 16, false);
        if (fileName != null && fileName.length() > 64) {
            fileName = fileName.substring(fileName.length() - 64);
        }
        if (type != null && type.length() > 0) {
            fileName = fileName + "." + type;
        }
        return fileName;
    }

    public static String digest(String content, String algorithm, int radix) {
        return digest(content, algorithm, radix, true);
    }

    public static String digest(String content, String algorithm, int radix, boolean fillZero) {
        if (content == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] bytes = content.getBytes(Charset.defaultCharset());
            digest.update(bytes);
            BigInteger bigInteger = new BigInteger(1, digest.digest());
            StringBuilder result = new StringBuilder(bigInteger.toString(radix));
            if (fillZero) {
                while (result.length() < 2 * bytes.length) {
                    result.insert(0, "0");
                }
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return content;
        }
    }

    @NonNull
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

    public static void notifyMediaChanged(Context context, Uri fileUri) {
        if (context == null || fileUri == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(fileUri);
        context.sendBroadcast(intent);
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
                File f = new File(folderPath, ze.getName());
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
        if (file == null || !file.exists()) {
            return true;
        }
        if (file.isDirectory()) {
            File[] ff = file.listFiles();
            if (ff != null && ff.length > 0) {
                for (File f : ff) {
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

    public static boolean copy(File srcFile, File destFile) {
        if (srcFile == null || destFile == null) {
            return false;
        }

        File destParent = destFile.getParentFile();
        if (destParent != null && !destParent.exists()) {
            destParent.mkdirs();
        }
        FileChannel fis = null;
        FileChannel fos = null;
        try {
            fis = new FileInputStream(srcFile).getChannel();
            fos = new FileOutputStream(destFile).getChannel();
            return fis.transferTo(0, fis.size(), fos) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            FileIOUtil.quickClose(fis);
            FileIOUtil.quickClose(fos);
        }
    }

    public static boolean copy(Context context, Uri srcUri, Uri destUri) {
        if (context == null || srcUri == null || destUri == null) {
            return false;
        }
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            outputStream = context.getContentResolver().openOutputStream(destUri);
            inputStream = context.getContentResolver().openInputStream(srcUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FileIOUtil.writeStream(inputStream, outputStream);
    }

    public static boolean copy(Context context, Uri srcUri, File destFile) {
        if (context == null || srcUri == null || destFile == null) {
            return false;
        }
        File destParent = destFile.getParentFile();
        if (destParent != null && !destParent.exists()) {
            destParent.mkdirs();
        }
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            outputStream = new FileOutputStream(destFile);
            inputStream = context.getContentResolver().openInputStream(srcUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FileIOUtil.writeStream(inputStream, outputStream);
    }

}