package com.fourfaith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @version V1.0
 * @ClassName
 * @Description 通信服务
 * @Author fourfaith_lwj1
 * @Date 2024-07-02 19:44
 */
@SpringBootApplication
@EnableAsync
public class NettyServerApplication {

    @Resource
    private RedisTemplate<String, Object> stringRedisTemplate;

    public static void main(String [] args){
        SpringApplication.run(NettyServerApplication.class, args);
    }

}
