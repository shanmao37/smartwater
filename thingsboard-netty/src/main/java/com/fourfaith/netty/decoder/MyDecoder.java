package com.fourfaith.netty.decoder;

import com.fourfaith.netty.encoder.MyEncoder;
import com.fourfaith.util.CRCUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @version V1.0
 * @ClassName
 * @Description
 * @Author fourfaith_lwj1
 * @Date 2024-07-03 17:03
 */
@Slf4j
public class MyDecoder extends ByteToMessageDecoder {
    private static StringBuffer MsgBuffer = new StringBuffer();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //创建字节数组,buffer.readableBytes可读字节长度
        byte[] b = new byte[byteBuf.readableBytes()];
        //复制内容到字节数组b
        byteBuf.readBytes(b);
        String msg = toHexString(b);
        msgHandle(msg,list);
    }

    public void msgHandle(String msg,List<Object> out){
        log.info("-----------------【MyDecoder.msgHandle】待处理的数据帧：{}", msg);
        // 判断是否需要拼接
        if (msg.indexOf("0a") > 0 && !(msg.indexOf("fefd") >= 0 && (msg.length() - msg.lastIndexOf("0d0a") == 4))) {
            MsgBuffer.append(msg);
            String msgBuf = MsgBuffer.toString();
            if (msgBuf.indexOf("fefd") >= 0 && (msgBuf.length() - msgBuf.lastIndexOf("0d0a") == 4)) {
                // 校验和处理拼接后的完整消息
                if (processMessage(msgBuf, out)) {
                    MsgBuffer.delete(0, MsgBuffer.length());
                } else {
                    MsgBuffer.delete(0, MsgBuffer.length());
                }
            }
            return;
        }else {
            processMessage(msg, out);
        }
    }


    // 处理完整的消息，返回是否校验成功
    private boolean processMessage(String msg, List<Object> out) {
        // 检查帧头和帧尾
        if (!msg.startsWith("fefd") || !msg.endsWith("0d0a")) {
            return false;
        }

        // 提取校验码
        String checkCode = msg.substring(8, 12);

        // 计算 CRC 校验码
        byte[] crcbyte = MyEncoder.hexString2Bytes(msg.substring(12));
        int crc = CRCUtil.calcCrc16(crcbyte);
        crc = CRCUtil.revert(crc);
        String crcCode = String.format("%04x", crc);

        // 比较校验码
        if (!checkCode.equals(crcCode)) {
            log.error("--------------【processMessage】CRC校验失败！服务端的校验码{}，设备端的校验码{}", crcCode, checkCode);
            return false;
        }

        // 校验通过，添加消息到输出列表
        out.add(msg);
        return true;
    }

    public String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i])+" ";
            if (sTemp.length() < 3){
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public static String toHexString(byte[] b) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < b.length; ++i) {
            buffer.append(toHexString(b[i]));
        }
        return buffer.toString();
    }

    public static String toHexString(byte b) {
        String s = Integer.toHexString(b & 0xFF);
        if (s.length() == 1) {
            return "0" + s;
        } else {
            return s;
        }
    }

    /**
     * 十六进制字符串转字符串
     *
     * @param hexStr 原16进制字符串
     * @return 字符串
     * */
    public static String decodeHex(String hexStr) {
        // 定义字符数组，用于保存字符串字符，长度为16进制字符串的一半
        byte[] strs = new byte[hexStr.length() / 2];
        // 遍历赋值
        for (int i = 0; i < strs.length; i++) {
            // 截取高位，使用Integer.parseInt进行转换
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            // 截取低位，使用Integer.parseInt进行转换
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            // 拼接赋值
            strs[i] = (byte)(high * 16 + low);
        }
        // 将字符数组转换为字符串，返回结果
        return new String(strs);
    }

    /**
     * 字符串转换成为16进制(无需Unicode编码)
     * @param str
     * @return
     */
    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();
    }


    /**
     * 16进制高低位转换
     * @param hex
     * @return java.lang.String
     * @author wangguangle
     * @date: 2021/4/8 19:33
     */
    public static String reverseHex(String hex) {
        char[] charArray = hex.toCharArray();
        int length = charArray.length;
        int times = length / 2;
        for (int c1i = 0; c1i < times; c1i += 2) {
            int c2i = c1i + 1;
            char c1 = charArray[c1i];
            char c2 = charArray[c2i];
            int c3i = length - c1i - 2;
            int c4i = length - c1i - 1;
            charArray[c1i] = charArray[c3i];
            charArray[c2i] = charArray[c4i];
            charArray[c3i] = c1;
            charArray[c4i] = c2;
        }
        return new String(charArray);
    }

    /**
     * 十六进制转ASCII码
     * @other > Integer.toHexString(int) -> 10 to 16
     * @param hex
     * @return
     */
    public static String convertHexToString(String hex) {

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        for (int i = 0; i < hex.length() - 1; i += 2) {

            // grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            // convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            // convert the decimal to character
            sb.append((char) decimal);

            temp.append(decimal);
        }
        return sb.toString();
    }

    /**
     * 十六进制转ASCII码对应值
     * @other
     * @param hexString
     * @return
     */
    public static String hexStringToAscii(String hexString) {

        int len = hexString.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i+1), 16));
        }
        String asciiString = new String(bytes);
        return asciiString;
    }

    /**
     * 十六进制转10进制 按位计算，位值乘权重
     * @Author @zzh
     * @Description // 十六进制转10进制
     * @Date 14:59 2023/5/4
     * @param hex
     * @return int
     **/
    public static int hexToDecimal(String hex) {
        int decimal = 0;
        String digits = "0123456789abcdef";
        for (int i = 0; i < hex.length(); i++) {
            char c = hex.charAt(i);
            int d = digits.indexOf(c);
            decimal = decimal * 16 + d;
        }
        return decimal;
    }

    /**
     * 汉字转GB2312
     *
     * @param chinese 汉字
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String StringToGb(String chinese) throws UnsupportedEncodingException {
        // 先把字符串按gb2312转成byte数组
        byte[] bytes = chinese.getBytes("GB2312");
        StringBuilder gbString = new StringBuilder();
        // 遍历数组
        for (byte b : bytes){
            // 再用Integer中的方法，把每个byte转换成16进制输出
            String temp = Integer.toHexString(b);
            // 截取
            if (temp.length() > 2){
                temp = temp.substring(6, 8);
            }else if (temp.length() == 2){
                // 为数字，数字的区为A3
                gbString.append("A3");
                temp = "B" + temp.substring(1);
            }
            gbString.append(temp);
        }
        return gbString.toString();
    }

    /**
     * GB2312转汉字
     *
     * @param string gb3212码
     * @return
     * @throws Exception
     */
    public static String GbToString(String string) throws Exception{
        byte[] bytes = new byte[string.length() / 2];
        for(int i = 0; i < bytes.length; i ++){
            byte high = Byte.parseByte(string.substring(i * 2, i * 2 + 1), 16);
            byte low = Byte.parseByte(string.substring(i * 2 + 1, i * 2 + 2), 16);
            bytes[i] = (byte) (high << 4 | low);
        }
        String result = new String(bytes, "GB2312");
        return result;
    }
}
