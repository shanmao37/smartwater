package com.fourfaith.netty.handler;

import com.fourfaith.netty.decoder.MyDecoder;
import com.fourfaith.netty.encoder.MyEncoder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @version V1.0
 * @ClassName
 * @Description
 * @Author fourfaith_lwj1
 * @Date 2024-07-03 17:01
 */
@Component
@ChannelHandler.Sharable
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast(new LineBasedFrameDecoder(1024,false,true));
        // 限制消息读写时间，如果超过指定时间未进行数据通讯，则认为客户端闲置离线
        pipeline.addLast(new IdleStateHandler(0,0,180, TimeUnit.SECONDS));
        // 字符串解码 和 编码
//        pipeline.addLast(new MyDecoder());
//        pipeline.addLast(new MyEncoder());
        // 自己的逻辑Handler
        pipeline.addLast("handler", new TcpHandler());
    }
}
