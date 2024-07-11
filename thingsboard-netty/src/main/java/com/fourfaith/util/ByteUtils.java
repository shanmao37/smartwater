package com.fourfaith.util;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 * @version V1.0
 * @ClassName
 * @Description
 * @Author fourfaith_lwj1
 * @Date 2024-07-10 10:40
 */
@Slf4j
public class ByteUtils {

    public  static String receiveHexToString(byte[] by) {
        try {
            String str = bytes2Str(by);
            str = str.toUpperCase();
            return str;
        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("接收字节数据并转为16进制字符串异常");
        }
        return null;
    }

    /**
     * 字节数组转换为16进制字符串
     * @param src
     * @return
     */
    public static String bytes2Str(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            if(i>0){
                stringBuilder.append(" ");
            }
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    public static byte[] intTo2ByteArray(int value) {
        byte[] result = new byte[2];
        // 使用ByteBuffer的采用大端模式，高位在前
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort((short) value); // 将int转换为short
        buffer.flip(); // 反转缓冲区，为读取做准备
        buffer.get(result); // 将数据读入字节数组
        return result;
    }

    public static int byteArrayToInt(byte[] byteArray) {
        int value = 0;
        for (int i = 0; i < byteArray.length; i++) {
            value = (value << 8) | (byteArray[i] & 0xFF);
        }
        return value;
    }


    public static byte[] mergeArrays(byte[]... arrays) {
        int totalLength = 0;
        for (byte[] array : arrays) {
            totalLength += array.length;
        }
        byte[] result = new byte[totalLength];
        int currentIndex = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, currentIndex, array.length);
            currentIndex += array.length;
        }
        return result;
    }
}
