package com.fourfaith.netty.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fourfaith.constant.ByteConstant;
import com.fourfaith.mqtt.MqttConnection;
import com.fourfaith.netty.response.PacketGenerateUtils;
import com.fourfaith.util.ByteUtils;
import com.fourfaith.util.CRCUtil;
import com.fourfaith.util.SpringContextUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.ByteBuffer;

import static com.fourfaith.netty.UdpServer.mqConnectPool;

/**
 * @version V1.0
 * @ClassName
 * @Description
 * @Author fourfaith_lwj1
 * @Date 2024-07-03 15:00
 */
@Slf4j
public class UdpHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private String host = "tcp://27.154.58.226:25138";
    private String topic = "fourfaith/device/rpc/request/+";

    public UdpHandler(String host,String topic){
        this.host = host;
        this.topic = topic;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //当channel就绪后。
        Channel incoming = ctx.channel();
        log.info("UDP-Client:" + incoming.remoteAddress() + "上线");
    }

    /**
     * 重写接收到的数据的具体操作
     * @param ctx
     * @param packet
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        try {
            ByteBuf buf = packet.content();

//            String str = byteBuf.toString(CharsetUtil.UTF_8);
            byte[] bytes = new byte[buf.readableBytes()];
            // 复制内容到字节数组bytes
            buf.readBytes(bytes);

            // 将接收到的数据转为字符串，此字符串就是客户端发送的字符串
            String str = ByteUtils.receiveHexToString(bytes);

            log.info("receive str: " + str);

            //包头校验 40 40
            byte b0 = buf.getByte(0);
            byte b1 = buf.getByte(1);
            if(!(b0 == ByteConstant.head && b1 == ByteConstant.head)){
                log.info("包头校验失败");
                return;
            }
            //协议版本号校验 01
            byte version = buf.getByte(2);
            if(version != ByteConstant.version){
                log.info("协议版本号校验失败");
                return;
            }

            //业务流水号 00
            byte serial= buf.getByte(3);
          /*  if(serial != ByteConstant.serial){
                log.info("业务流水号校验失败");
                return;
            }*/

            //命令字节 00 注册，01数据，02平安，03命令
            byte command= buf.getByte(4);

            //子命令字节 01
            byte subCommand= buf.getByte(5);

            //大类码+设备类型+设备ID
            byte[] deviceCodeArr = ByteBufUtil.getBytes(buf,6,6);
            String deviceCode = ByteUtils.receiveHexToString(deviceCodeArr).replaceAll(" ","");
            log.info("设备编号："+deviceCode);

            //应用数据单元长度
            byte[] lengthArr = ByteBufUtil.getBytes(buf,12,2);
            String length = ByteUtils.receiveHexToString(lengthArr);
            Integer lengthInt = ByteUtils.byteArrayToInt(lengthArr);
            log.info("应用数据单元长度："+length);
            log.info("应用数据单元长度转数值："+lengthInt);

            //CRC16
            byte[] crc16 = ByteBufUtil.getBytes(buf,bytes.length-1,1);
            String crc16Str = ByteUtils.receiveHexToString(crc16);
            byte sourceCRC = crc16[0];
            log.info("CRC16值："+crc16Str);

            //获取原文
            byte[] content =  ByteBufUtil.getBytes(buf,2,bytes.length-3);
            String contentStr = ByteUtils.receiveHexToString(content);
            log.info("content："+contentStr);

            //校验CRC16
            byte checksum = CRCUtil.calcChecksum(content,content.length);
            boolean flag = sourceCRC == checksum;
            log.info("CRC16校验："+flag);
            if(!flag){
                log.info("CRC16校验校验失败");
                return;
            }

            MqttConnection connection = mqConnectPool.get(deviceCode);
            if(connection == null){
                connection = new MqttConnection(host,topic,deviceCode,"",deviceCode);
                boolean conFlag = connection.init();
                if(conFlag){
                    mqConnectPool.put(deviceCode,connection);
                }
            }

            byte[] retBytes = new byte[]{};
            if(command == ByteConstant.registerCode){
                retBytes = PacketGenerateUtils.loadPacket(command,new byte[]{ByteConstant.success},deviceCodeArr);
            }else if(command == ByteConstant.dataCode || command == ByteConstant.safeCode){
                retBytes = PacketGenerateUtils.loadPacket(command,new byte[]{ByteConstant.success});
            }

            String retStr = ByteUtils.receiveHexToString(retBytes);
            //应答
            log.info("return retStr: " + retStr);

            DatagramPacket resData = new DatagramPacket(Unpooled.copiedBuffer(retBytes), packet.sender());
            ctx.writeAndFlush(resData);

            String temp = "40 40 01 00 03 01 00 06 41 54 2B 56 45 52 08".replaceAll(" ","");

            byte[] tempB = hexStringToByteArray(temp);
            String tempStr = ByteUtils.receiveHexToString(tempB);
            //应答
            log.info("return tempStr: " + tempStr);
            DatagramPacket resAtData = new DatagramPacket(Unpooled.copiedBuffer(tempB), packet.sender());
            ctx.writeAndFlush(resAtData);

         /*   //获取指令数据
            RedisTemplate<String, Object>  stringRedisTemplate = (RedisTemplate)SpringContextUtil.getBean("redisTemplate");
            String redisVal = (String)stringRedisTemplate.opsForValue().get(deviceCode);
            log.info("redisVal："+redisVal);
            if(StringUtils.isNotBlank(redisVal)){
                JSONObject jsonObject = JSON.parseObject(redisVal);
                String atStr = jsonObject.getString("method")+"="+jsonObject.getJSONObject("params").getString("value");
                log.info("atStr："+atStr);
                log.info("存在AT指令，开始下发");
                byte[] atCommand = PacketGenerateUtils.loadPacket(ByteConstant.commandCode,atStr.getBytes());
                String retAtStr = ByteUtils.receiveHexToString(atCommand);
                //应答
                log.info("return retAtStr: " + retAtStr);

                DatagramPacket resAtData = new DatagramPacket(Unpooled.copiedBuffer(atCommand), packet.sender());
                ctx.writeAndFlush(resAtData);
                stringRedisTemplate.delete(deviceCode);
            }
*/
        } catch (Exception e) {
            log.error("",e);
        }
    }

    /**
     * 出错回调
     * @param channelHandlerContext
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        log.error("",cause);
        channelHandlerContext.close();
    }

    // 将十六进制字符串转换为字节数组
    private static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }


}
