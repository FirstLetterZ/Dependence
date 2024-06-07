package com.zpf.tool;

import java.nio.ByteBuffer;

public class CameraBitmapUtil {

    public static void writeRgba(int[] pixels, int imageWidth, int imageHeight, ByteBuffer buffer, boolean needRotate) {
        int n;
        int i;
        for (int row = 0; row < imageHeight; row++) {
            for (int col = 0; col < imageWidth; col++) {
                i = row * imageWidth + col;
                n = 4 * i;
                int r = buffer.get(n) & 0xff;
                int g = buffer.get(n + 1) & 0xff;
                int b = buffer.get(n + 2) & 0xff;
                int a = buffer.get(n + 3) & 0xff;
                int color = a << 24 | r << 16 | g << 8 | b;
                if (needRotate) {
                    i = imageHeight - 1 - row + imageHeight * col;
                }
                pixels[i] = color;
            }
        }
    }

    public static void writeYuv420(int[] pixels, int imageWidth, int imageHeight, ByteBuffer yBuffer, ByteBuffer uBuffer, ByteBuffer vBuffer, boolean needRotate) {
        int y = 0;
        int u = 0;
        int v = 0;
        int i;
        for (int row = 0; row < imageHeight; row++) {
            for (int col = 0; col < imageWidth; col++) {
                i = row * imageWidth + col;
                y = yBuffer.get(i) & 0xff; // Y 数组是紧密排布的
                if ((col & 0x1) == 0) { // UV 每行的奇数元素是有用的，偶数位不要
                    int k = row / 2 * imageWidth + col;
                    u = uBuffer.get(k) & 0xff; // U 是两行合并一行的
                    v = vBuffer.get(k) & 0xff; // V 和U是一样的
                }
                int facter = 128;
                int r = (int) (y + 1.4022 * (v - facter));
                int g = (int) (y - 0.34414 * (u - facter) - 0.71414 * (v - facter));
                int b = (int) (y + 1.772 * (u - facter));
                r = r < 0 ? 0 : (Math.min(r, 255));
                g = g < 0 ? 0 : (Math.min(g, 255));
                b = b < 0 ? 0 : (Math.min(b, 255));
                int color = 0xff000000 | r << 16 | g << 8 | b;
                if (needRotate) {
                    i = imageHeight - 1 - row + imageHeight * col;
                }
                pixels[i] = color;
            }
        }
    }

}