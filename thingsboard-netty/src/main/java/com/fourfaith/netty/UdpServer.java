package com.fourfaith.netty;

import com.fourfaith.mqtt.MqttConnection;
import com.fourfaith.netty.handler.UdpHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @version V1.0
 * @ClassName
 * @Description
 * @Author fourfaith_lwj1
 * @Date 2024-07-03 11:40
 */
@Slf4j
@Component
public class UdpServer {
    public static ConcurrentHashMap<String, MqttConnection> mqConnectPool = new ConcurrentHashMap<>();

    /**
     * 启动服务
     */
    public void run(int port,String host,String topic) {
        //表示服务器连接监听线程组，专门接受 accept 新的客户端client 连接
        EventLoopGroup bossLoopGroup = new NioEventLoopGroup();
        try {
            //1、创建netty bootstrap 启动类
            Bootstrap bootstrap = new Bootstrap();
            //2、设置boostrap 的eventLoopGroup线程组
            bootstrap.group(bossLoopGroup)
            //3、设置NIO UDP连接通道
            .channel(NioDatagramChannel.class)
            //4、设置通道参数 SO_BROADCAST广播形式
            .option(ChannelOption.SO_BROADCAST, true)
            //5、设置处理类 装配流水线
            .handler(new UdpHandler(host,topic));
            //6、绑定server，通过调用sync（）方法异步阻塞，直到绑定成功
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            log.info("netty Udp服务器启动成功（port:" + port + "）......");
            //7、监听通道关闭事件，应用程序会一直等待，直到channel关闭
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("",e);
        } finally {
            log.info("netty udp close!");
            //8 关闭EventLoopGroup，
            bossLoopGroup.shutdownGracefully();
        }
    }
}
