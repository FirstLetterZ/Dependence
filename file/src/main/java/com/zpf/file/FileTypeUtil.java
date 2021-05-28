package com.zpf.file;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 常用文件的文件头如下：(以前六位为准)
 * JPEG (jpg)，文件头：FFD8FF
 * PNG (png)，文件头：89504E47
 * GIF (gif)，文件头：47494638
 * TIFF (tif)，文件头：49492A00
 * Windows Bitmap (bmp)，文件头：424D
 * CAD (dwg)，文件头：41433130
 * Adobe Photoshop (psd)，文件头：38425053
 * Rich Text Format (rtf)，文件头：7B5C727466
 * XML (xml)，文件头：3C3F786D6C
 * HTML (html)，文件头：68746D6C3E
 * Email [thorough only] (eml)，文件头：44656C69766572792D646174653A
 * Outlook Express (dbx)，文件头：CFAD12FEC5FD746F
 * Outlook (pst)，文件头：2142444E
 * MS Word/Excel (xls.or.doc)，文件头：D0CF11E0
 * MS Access (mdb)，文件头：5374616E64617264204A
 * WordPerfect (wpd)，文件头：FF575043
 * Postscript (eps.or.ps)，文件头：252150532D41646F6265
 * Adobe Acrobat (pdf)，文件头：255044462D312E
 * Quicken (qdf)，文件头：AC9EBD8F
 * Windows Password (pwl)，文件头：E3828596
 * ZIP Archive (zip)，文件头：504B0304
 * RAR Archive (rar)，文件头：52617221
 * Wave (wav)，文件头：57415645
 * AVI (avi)，文件头：41564920
 * Real Audio (ram)，文件头：2E7261FD
 * Real Media (rm)，文件头：2E524D46
 * MPEG (mpg)，文件头：000001BA
 * MPEG (mpg)，文件头：000001B3
 * Quicktime (mov)，文件头：6D6F6F76
 * Windows Media (asf)，文件头：3026B2758E66CF11
 * MIDI (mid)，文件头：4D546864
 */
public class FileTypeUtil {

    public static int readFileHeadCode(File file) {
        return getHeadCode(readFileHeadString(file));
    }

    public static int readFileHeadCode(Context context, Uri fileUri) {
        return getHeadCode(readFileHeadString(context, fileUri));
    }

    @FileType
    public static int getHeadCode(String head) {
        if (head == null) {
            return FileType.UNKNOWN;
        }
        head = head.toUpperCase();
        if (head.startsWith("FFD8FF")) {
            return FileType.JPEG;
        } else if (head.startsWith("89504E")) {
            return FileType.PNG;
        } else if (head.startsWith("474946")) {
            return FileType.GIF;
        } else if (head.startsWith("524946")) {
            return FileType.WEBP;
        } else if (head.startsWith("49492A00")) {
            return FileType.TIFF;
        } else if (head.startsWith("424D")) {
            return FileType.BMP;
        } else if (head.startsWith("3C3F786D6C")) {
            return FileType.XML;
        } else if (head.startsWith("68746D6C3E")) {
            return FileType.HTML;
        } else if (head.startsWith("255044462D312E")) {
            return FileType.PDF;
        } else if (head.startsWith("504B0304")) {
            return FileType.ZIP;
        } else if (head.startsWith("52617221")) {
            return FileType.RAR;
        } else if (head.startsWith("57415645")) {
            return FileType.WAV;
        } else if (head.startsWith("41564920")) {
            return FileType.AVI;
        } else if (head.startsWith("2E524D46")) {
            return FileType.RM;
        } else if (head.startsWith("000001B")) {
            return FileType.MPG;
        } else {
            return FileType.OTHER;
        }
    }

    public static String readFileHeadString(File file) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
            FileIOUtil.quickClose(inputStream);
            return null;
        }
        byte[] bytes = FileIOUtil.readStreamBytes(inputStream, 8);
        return bytesToHexString(bytes);
    }

    public static String readFileHeadString(Context context, Uri fileUri) {
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(fileUri);
        } catch (Exception e) {
            e.printStackTrace();
            FileIOUtil.quickClose(inputStream);
            return null;
        }
        byte[] bytes = FileIOUtil.readStreamBytes(inputStream, 8);
        return bytesToHexString(bytes);
    }

    private static String bytesToHexString(byte[] src) {
        if (src == null || src.length <= 0) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : src) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

}