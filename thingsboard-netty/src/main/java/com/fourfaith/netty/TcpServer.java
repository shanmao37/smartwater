package com.fourfaith.netty;

import com.fourfaith.netty.handler.NettyServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version V1.0
 * @ClassName
 * @Description
 * @Author fourfaith_lwj1
 * @Date 2024-07-03 16:58
 */
@Slf4j
public class TcpServer {
    /**
     * 启动服务
     */
    public void run(String address,int port){
        //创建两个线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(); //8个NioEventLoop

        try {
            ServerBootstrap b = new ServerBootstrap();
            /*将线程组传入*/
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)/*指定使用NIO进行网络传输*/
                    .option(ChannelOption.SO_BACKLOG, 128) // 设置线程的连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    /*服务端每接收到一个连接请求，就会新启一个socket通信，也就是channel，
                    所以下面这段代码的作用就是为这个子channel增加handle*/
                    .childHandler(new NettyServerInitializer());
            log.info("netty Tcp服务器启动成功（port:" + port + "）......");
            /*异步绑定到服务器，sync()会阻塞直到完成*/
            ChannelFuture channelFuture = b.bind(port).sync();
            //监听关闭 /*阻塞直到服务器的channel关闭*/
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("",e);
        } finally {
            /**关闭线程组*/
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
