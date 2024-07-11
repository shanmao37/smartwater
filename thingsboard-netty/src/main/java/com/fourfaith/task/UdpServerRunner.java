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
 * @Date 2024-07-03 18:01
 */
@Component
@Slf4j
public class UdpServerRunner implements CommandLineRunner {
    @Value("${thingsboard.netty.address}")
    private String address;
    @Value("${thingsboard.netty.udp.port}")
    private int udpPort;
    @Value("${mqtt.host}")
    private String host;
    @Value("${mqtt.topic}")
    private String topic;

    @Async
    @Override
    public void run(String... args) throws Exception {
        log.info("-------UdpServerRunner---address:{},udpPort:{},host:{},topic:{}",address,udpPort,host,topic);
        UdpServer udpServer = new UdpServer();
        udpServer.run(udpPort,host,topic);
    }
}
