package com.fourfaith.constant;

/**
 * @version V1.0
 * @ClassName
 * @Description
 * @Author fourfaith_lwj1
 * @Date 2024-07-08 18:24
 */
public class ByteConstant {
    //包头
    public final static byte head = 0x40;
    //协议版本号
    public final static byte version = 0x01;
    //业务流水号
    public final static byte serial = 0x00;

    //命令字节
    //注册报
    public final static byte registerCode = 0x00;
    //数据采集报
    public final static byte dataCode = 0x01;
    //平安报
    public final static byte safeCode = 0x02;
    //命令报
    public final static byte commandCode = 0x03;

    //子命令字节
    public final static byte subCommand = 0x01;


    //返回结果
    public final static byte success = 0x00;
    public final static byte fail = 0x01;
    public final static byte tokenFail = 0x02;

}
