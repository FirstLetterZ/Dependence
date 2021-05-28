package com.zpf.update;

import android.content.Context;

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
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * @author Created by ZPF on 2021/3/29.
 */
public class UpdateUtil {

    public static boolean isNotEmptyDirectory(File folder) {
        return folder != null && folder.isDirectory() && folder.list().length > 0;
    }

    public static String getRootFolderPath(Context context, String fileGroupId) {
        return UpdateUtil.getAppDataPath(context) + File.separator + UpdateUtil.md5String(fileGroupId);
    }

    public static String getAppDataPath(Context context) {
        File dataFileDir = context.getExternalFilesDir("dataFiles");
        if (dataFileDir != null && dataFileDir.exists()) {
            return dataFileDir.getAbsolutePath();
        } else {
            return context.getFilesDir().getAbsolutePath();
        }
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

    public static void deleteOtherFolder(File baseFolder, List<String> excludePath) {
        if (!baseFolder.exists() || !baseFolder.isDirectory()) {
            return;
        }
        for (File file : baseFolder.listFiles()) {
            if (file.isDirectory() && (excludePath == null || !excludePath.contains(file.getAbsolutePath()))) {
                deleteFolder(file);
            }
        }
    }

    //删除文件夹和文件夹里面的文件
    public static void deleteFolder(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete();
            else if (file.isDirectory()) {
                deleteFolder(file);
            }
        }
        dir.delete();// 删除目录本身
    }

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte[] buffer = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            return new BigInteger(1, digest.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    //
                }
            }
        }
    }

    public static String md5String(String content) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(content.getBytes("UTF-8"));
            return new BigInteger(1, digest.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean copyFromAsset(Context context, String assetPath, String outDirPath) {
        ZipInputStream zis = null;
        FileOutputStream fos = null;
        try {
            zis = new ZipInputStream(context.getAssets().open(assetPath));
            ZipEntry zipEntry = zis.getNextEntry();
            byte[] buffer = new byte[4096];
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    File newDir = new File(outDirPath, fileName);
                    newDir.mkdirs();
                } else {
                    File newFile = new File(outDirPath, fileName);
                    fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.flush();
                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (zis != null) {
                try {
                    zis.close();
                } catch (IOException e) {
                    //
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    //
                }
            }
        }
        return true;
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

}
