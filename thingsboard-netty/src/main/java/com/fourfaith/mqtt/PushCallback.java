package com.fourfaith.mqtt;


import com.fourfaith.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @Title
 * @Author ZQS
 * @Date 2019/10/9 16:08
 * @Description
 */
@Slf4j
public class PushCallback implements MqttCallback {

    private MqttConnection mqttConn;

    private String redisKey;

    public PushCallback() {

    }

    public PushCallback(MqttConnection mqttConn,String redisKey) {
        this.mqttConn = mqttConn;
        this.redisKey = redisKey;
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.info("[MQTT] 连接断开，30S之后尝试重连...");
        while (true) {
            try {
                log.info("[MQTT] 连接断开，线程执行开始");
                Thread.sleep(30000);
                boolean flag = mqttConn.init();
                if(flag){
                    log.info("[MQTT] 连接断开，线程执行结束");
                    break;
                }
            } catch (Exception e) {
                log.error("[MQTT]" + e.getMessage());
                continue;
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        log.info("deliveryComplete---------" + token.isComplete());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        // subscribe后得到的消息会执行到这里面
        log.info("接收主题 : "+topic+"，消息："+message);
        RedisTemplate<String, Object> stringRedisTemplate = (RedisTemplate) SpringContextUtil.getBean("redisTemplate");
        stringRedisTemplate.opsForValue().set(redisKey,message.toString(),1, TimeUnit.DAYS);
    }


}
