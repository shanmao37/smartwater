package com.fourfaith.task;

import com.fourfaith.netty.TcpServer;
import com.fourfaith.netty.UdpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @version V1.0
 * @ClassName
 * @Description
 * @Author fourfaith_lwj1
 * @Date 2024-07-03 16:55
 */
@Component
@Slf4j
public class TcpServerRunner implements CommandLineRunner {
    @Value("${thingsboard.netty.address}")
    private String address;
    @Value("${thingsboard.netty.tcp.port}")
    private int tcpPort;

    @Async
    @Override
    public void run(String... args) throws Exception {
        log.info("-------TcpServerRunner---address:{},tcpPort:{}",address,tcpPort);
        TcpServer tcpServer = new TcpServer();
        tcpServer.run(address,tcpPort);
    }
}
