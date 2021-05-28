package com.zpf.file;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Created by ZPF on 2021/4/8.
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FileType {
    int UNKNOWN = -1;
    int OTHER = 0;
    int JPEG = 1;
    int PNG = 2;
    int WEBP = 3;
    int GIF = 4;
    int TIFF = 5;
    int BMP = 6;
    int XML = 7;
    int HTML = 8;
    int PDF = 9;
    int ZIP = 10;
    int RAR = 11;
    int WAV = 12;
    int AVI = 13;
    int RM = 14;
    int MPG = 15;
}
