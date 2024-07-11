package com.fourfaith.netty.response;

import com.fourfaith.constant.ByteConstant;
import com.fourfaith.util.ByteUtils;
import com.fourfaith.util.CRCUtil;

/**
 * @version V1.0
 * @ClassName 报文封装
 * @Description
 * @Author fourfaith_lwj1
 * @Date 2024-07-10 10:20
 */
public class PacketGenerateUtils {


    public static byte[] loadPacket(byte command,byte[]... arrays){
        byte[] headBytes = new byte[]{ByteConstant.head,ByteConstant.head};
        byte[] versionBytes = new byte[]{ByteConstant.version,ByteConstant.serial,command,ByteConstant.subCommand};
        //组装内容体
        byte[] contentBytes = ByteUtils.mergeArrays(arrays);
        int length = contentBytes.length;
        byte[] lenBytes = ByteUtils.intTo2ByteArray(length);
        //控制单元和数据应用单元
        contentBytes = ByteUtils.mergeArrays(versionBytes,lenBytes,contentBytes);
        //生成crc
        byte retCrc = CRCUtil.calcChecksum(contentBytes,contentBytes.length);
        //合并数组
        byte[] retBytes = ByteUtils.mergeArrays(headBytes,contentBytes,new byte[]{retCrc});
        return retBytes;
    }
}
