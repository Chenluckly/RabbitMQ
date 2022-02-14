package com.wanghu.util;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author wanghu
 * @date 2022/1/31 17:14
 */
public class RabbitMqUtils {
    public static Channel getChannel()throws Exception   {
        //创建一个连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //工厂ip连接rabbitmq的队列
        connectionFactory.setHost("192.168.1.107");
        //用户名
        connectionFactory.setUsername("admin");
        //密码
        connectionFactory.setPassword("123456");
        //创建连接
        com.rabbitmq.client.Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        return channel;
    }
}
