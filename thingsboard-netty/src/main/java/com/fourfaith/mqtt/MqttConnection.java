package com.fourfaith.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


/**
 * @Title
 * @Author ZQS
 * @Date 2019/10/9 16:09
 * @Description
 */
@Slf4j
public class MqttConnection {
    private String HOST ;
    private String TOPIC ;
    private String userName ;
    private String passWord;
    private String redisKey;
    private static final String clientid = "swClient";
    private MqttClient client;
    public MqttConnection(String HOST, String TOPIC, String userName, String passWord,String redisKey) {
        this.HOST = HOST;
        this.TOPIC = TOPIC;
        this.userName = userName;
        this.passWord = passWord;
        this.redisKey = redisKey;
    }

    //生成配置对象，用户名，密码等
    public MqttConnectOptions getOptions() {
        MqttConnectOptions  options = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setUserName(userName);
        options.setPassword(passWord.toCharArray());
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(20);
        return options;
    }
    public boolean connect() {
        //防止重复创建MQTTClient实例
        try {
            log.info("[mqtt] --- 连接");
            if (client == null) {
                log.info("[mqtt] --- 实例开始创建");
                client = new MqttClient(HOST, clientid, new MemoryPersistence());
                client.setCallback(new PushCallback(MqttConnection.this,redisKey));
            }
            log.info("[mqtt] --- 实例创建完成");
            MqttConnectOptions options = getOptions();
            //判断拦截状态，这里注意一下，如果没有这个判断，是非常坑的
            if (!client.isConnected()) {
                client.connect(options);
                log.info("[mqtt] --- 连接成功");
            } else {//这里的逻辑是如果连接成功就重新连接
                client.disconnect();
                client.connect(options);
                log.info("[mqtt] --- 连接成功");
            }
            //订阅消息
            int[] Qos  = {1};
            String[] topic1 = {TOPIC};
            client.subscribe(topic1, Qos);
        }catch (MqttException e){
            log.error("[mqtt] --- 连接错误："+e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }
    //监听设备发来的消息
    public boolean init() {
        return connect();
    }

}
