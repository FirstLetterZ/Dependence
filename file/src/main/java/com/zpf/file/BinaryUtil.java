package com.zpf.file;

/**
 * @author Created by ZPF on 2021/5/17.
 */
public class BinaryUtil {

    private static final String BINARY_SEPARATOR = " ";

    //字符串转换为二进制字符串
    public static String strToBinary(String str) {
        if (str == null) return null;
        StringBuilder sb = new StringBuilder();
        byte[] bytes = str.getBytes();
        for (byte aByte : bytes) {
            sb.append(Integer.toBinaryString(aByte)).append(BINARY_SEPARATOR);
        }
        return sb.toString();
    }


    //二进制字符串转换为普通字符串
    public static String binaryToStr(String binaryStr) {
        if (binaryStr == null) return null;
        String[] binArrays = binaryStr.split(BINARY_SEPARATOR);
        StringBuilder sb = new StringBuilder();
        for (String binStr : binArrays) {
            char c = binaryToChar(binStr);
            sb.append(c);
        }
        return sb.toString();
    }

    //二进制字符转换为int数组
    private static int[] binaryToIntArray(String binaryStr) {
        char[] temp = binaryStr.toCharArray();
        int[] result = new int[temp.length];
        for (int i = 0; i < temp.length; i++) {
            result[i] = temp[i] - 48;
        }
        return result;
    }

    // 将二进制转换成字符
    private static char binaryToChar(String binaryStr) {
        int[] temp = binaryToIntArray(binaryStr);
        int sum = 0;
        for (int i = 0; i < temp.length; i++) {
            sum += temp[temp.length - 1 - i] << i;
        }
        return (char) sum;
    }
}
