package com.zpf.file;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Base64;

import androidx.annotation.Nullable;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * @author Created by ZPF on 2021/5/28.
 */
public class FileIOUtil {

    public static boolean writeToFile(InputStream inputStream, File destFile, boolean append) {
        if (destFile == null || inputStream == null) {
            quickClose(inputStream);
            return false;
        }
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(destFile, append);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (outputStream == null) {
            return false;
        }
        return writeStream(inputStream, outputStream);
    }

    public static boolean writeToFile(String content, Charset charset, File destFile, boolean append) {
        if (destFile == null || content == null) {
            return false;
        }
        if (charset == null) {
            charset = Charset.defaultCharset();
        }
        try {
            byte[] data = content.getBytes(charset);
            return writeToFile(data, destFile, append);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean writeToFile(byte[] data, File destFile, boolean append) {
        if (destFile == null || data == null) {
            return false;
        }
        BufferedOutputStream bufferedOut = null;
        try {
            bufferedOut = new BufferedOutputStream(new FileOutputStream(destFile, append));
            bufferedOut.write(data);
            bufferedOut.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            quickClose(bufferedOut);
        }
    }

    public static boolean writeStream(InputStream inputStream, OutputStream outputStream) {
        if (inputStream == null || outputStream == null) {
            quickClose(inputStream);
            quickClose(outputStream);
            return false;
        }
        if (inputStream instanceof FileInputStream && outputStream instanceof FileOutputStream) {
            FileChannel fis = null;
            FileChannel fos = null;
            try {
                fis = ((FileInputStream) inputStream).getChannel();
                fos = ((FileOutputStream) outputStream).getChannel();
                return fis.transferTo(0, fis.size(), fos) > 0;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                quickClose(fis);
                quickClose(fos);
            }
        } else {
            byte[] buf = new byte[2048];
            int len;
            try {
                while ((len = inputStream.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                    outputStream.flush();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                quickClose(inputStream);
                quickClose(outputStream);
            }
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
            quickClose(fis);
            quickClose(fos);
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
        return writeStream(inputStream, outputStream);
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
        return writeStream(inputStream, outputStream);
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
            quickClose(out);
            e.printStackTrace();
        }
        return false;
    }

    @Nullable
    public static byte[] readFileBytes(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            return readStreamBytes(fis);
        } catch (Exception e) {
            e.printStackTrace();
            quickClose(fis);
        }
        return null;
    }

    @Nullable
    public static byte[] readFileBytes(File file, int position, int length) {
        if (position < 0 || length <= 0 || file == null) {
            return null;
        }
        RandomAccessFile accessFile = null;
        try {
            accessFile = new RandomAccessFile(file, "r");
            accessFile.seek(0);
            byte[] reads = new byte[length];
            accessFile.read(reads);
            return reads;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            quickClose(accessFile);
        }
    }

    @Nullable
    public static byte[] readStreamBytes(InputStream inputStream) {
        return readStreamBytes(inputStream, -1);
    }

    public static byte[] readStreamBytes(InputStream inputStream, int readCount) {
        if (inputStream == null) {
            return null;
        }
        try {
            if (readCount <= 0) {
                readCount = inputStream.available();
            }
            byte[] buffer = new byte[readCount];
            int temp;
            int offset = 0;
            int maxTime = 10000;
            while (offset < readCount) {
                if (maxTime < 0) {
                    throw new IOException("failed to complete after 10000 reads;");
                }
                temp = inputStream.read(buffer, offset, readCount - offset);
                if (temp < 0) {
                    break;
                }
                offset += temp;
                maxTime--;
            }
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
            quickClose(inputStream);
        }
        return null;
    }

    @Nullable
    public static String readAssetString(Context context, String fileName) {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            inputStream = context.getAssets().open(fileName);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder sb = new StringBuilder();
            do {
                line = bufferedReader.readLine();
                if (line != null) {
                    sb.append(line);
                }
            } while (line != null);

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            quickClose(inputStream);
            quickClose(bufferedReader);
        }
        return null;
    }

    public static void quickClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                //
            }
        }
    }

    @Nullable
    public String encodeBase64(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    @Nullable
    public String encodeBinary(byte[] bytes, Charset charset) {
        if (bytes == null) {
            return null;
        }
        if (charset == null) {
            charset = Charset.defaultCharset();
        }
        return BinaryUtil.binaryToStr(new String(bytes, charset));
    }

    @Nullable
    public String encodeBytes(byte[] bytes, Charset charset) {
        if (bytes == null) {
            return null;
        }
        if (charset == null) {
            charset = Charset.defaultCharset();
        }
        return new String(bytes, charset);
    }

    @Nullable
    public byte[] decodeBase64(String content, Charset charset) {
        if (content == null) {
            return null;
        }
        if (charset == null) {
            charset = Charset.defaultCharset();
        }
        return Base64.decode(content.getBytes(charset), Base64.DEFAULT);
    }

    @Nullable
    public byte[] decodeBinary(String content) {
        if (content == null) {
            return null;
        }
        String binaryString = BinaryUtil.strToBinary(content);
        return decodeString(binaryString, null);
    }

    @Nullable
    public byte[] decodeString(String content, Charset charset) {
        if (content == null) {
            return null;
        }
        if (charset == null) {
            charset = Charset.defaultCharset();
        }
        return content.getBytes(charset);
    }

}
