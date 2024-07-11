package com.fourfaith.netty.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @version V1.0
 * @ClassName
 * @Description
 * @Author fourfaith_lwj1
 * @Date 2024-07-03 17:10
 */
@Slf4j
public class MyEncoder extends MessageToByteEncoder<String> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, String s, ByteBuf byteBuf) throws Exception {
        //将16进制字符串转为数组
        byteBuf.writeBytes(hexString2Bytes(s));
    }

    /**
     * @Title:hexString2Bytes
     * @Description:16进制字符串转字节数组
     * @param src 16进制字符串
     * @return 字节数组
     */
    public static byte[] hexString2Bytes(String src) {
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = (byte) Integer.valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return ret;
    }

    /**
     * 计算出返回码的数据
     * @param str
     */
    public static String getCountNum(String str){
        StringBuilder sb = new StringBuilder();
        byte[] bytes =  hexString2Bytes(str);
        for (int i=bytes.length-3;i>13;i--){
            bytes[i] = (byte) (bytes[i] - 51);
            sb.append(Integer.toHexString(0xFF & bytes[i]));
        }
        return sb.toString();
    }
}
