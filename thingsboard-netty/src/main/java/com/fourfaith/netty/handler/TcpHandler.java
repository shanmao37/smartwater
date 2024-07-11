package com.fourfaith.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;

/**
 * @version V1.0
 * @ClassName
 * @Description
 * @Author fourfaith_lwj1
 * @Date 2024-07-03 17:26
 */
@Slf4j
@Component
/*不加这个注解那么在增加到childHandler时就必须new出来*/
@ChannelHandler.Sharable
public class TcpHandler extends SimpleChannelInboundHandler<Object> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        String message=msg.toString();
        log.info("----------设备交互数据[channelRead0]---------" + message);
    }
    /*
     * 数据读取完毕
     *
     * 覆盖 channelActive 方法 在channel被启用的时候触发 (在建立连接的时候)
     *
     * */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接的客户端地址 : " + ctx.channel().remoteAddress() + " active !");
        // 获取业务键（假设从Channel获取业务键的方法）
        String bizKey = ctx.channel().id().asLongText();
        super.channelActive(ctx);
    }

    //表示服务端与客户端连接建立
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();  //其实相当于一个connection

        /**
         * 调用channelGroup的writeAndFlush其实就相当于channelGroup中的每个channel都writeAndFlush
         *
         * 先去广播，再将自己加入到channelGroup中
         */
        log.info(" 【服务器】 -" +channel.remoteAddress() +" 加入\n");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIpPort = remoteAddress.getAddress().getHostAddress() + ":" + remoteAddress.getPort();
        log.info("Client IP:Port: {}", clientIpPort);
        log.info("----------设备下线[channelInactive]----------");
        super.channelInactive(ctx);
    }

    /** 发生异常后的处理*/
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIpPort = remoteAddress.getAddress().getHostAddress() + ":" + remoteAddress.getPort();
        log.info("Client IP:Port: {}", clientIpPort);
        if (ctx.channel().isActive()) {
            if (cause instanceof SocketTimeoutException) {
                log.error("----------设备连接超时异常下线[exceptionCaught]----------");
            } else if (cause instanceof IOException) {
                log.error("----------设备网络异常下线[exceptionCaught]----------");
                log.error("Network exception occurred: {}", cause.getMessage(), cause);
            } else {
                log.error("----------设备未知异常下线[exceptionCaught]----------");
                log.error("Unexpected exception occurred: {}", cause.getMessage(), cause);
            }

            log.error("----------设备异常下线，移除会话[exceptionCaught],{}", ctx.channel());
            ctx.close();
            log.info("----------设备异常下线，关闭通道[exceptionCaught]----------");
        } else {
            log.error("----------Channel已经不活跃,无需处理下线逻辑[exceptionCaught]----------");
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIpPort = remoteAddress.getAddress().getHostAddress() + ":" + remoteAddress.getPort();
        log.info("Client IP:Port: {}", clientIpPort);
        if (ctx.channel().isActive()) {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                switch (event.state()) {
                    case READER_IDLE:
                        log.info("----------客户端读取闲置超时异常下线[READER_IDLE]----------");
                        break;
                    case WRITER_IDLE:
                        log.info("----------客户端写入闲置超时异常下线[WRITER_IDLE]----------");
                        break;
                    case ALL_IDLE:
                        log.info("----------客户端全部闲置超时异常下线[ALL_IDLE]----------");
                        break;
                    default:
                        log.info("---------客户端闲置超时异常下线,IdleState.{} ----------", event.state());
                        break;
                }
                ctx.channel().close();
            } else {
                super.userEventTriggered(ctx, evt);
                log.info("----------客户端闲置超时异常下线,evt不是IdleStateEvent类型的事件,未清除通道信息[ALL_IDLE]----------{}", evt.getClass().getName());
            }
        } else {
            log.info("----------Channel已经不活跃,无需处理下线逻辑[userEventTriggered]----------");
        }
    }

}
