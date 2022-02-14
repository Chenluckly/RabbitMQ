package com.wanghu.one;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author wanghu
 * @date 2022/1/31 16:24
 */
public class Consumer {
    //队列的名称
    private static final String QUEUE_NAME="hello";

    //接收消息
    public static void main(String[] args) {
        //创建一个连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //工厂ip连接rabbitmq的队列
        connectionFactory.setHost("192.168.1.107");
        //用户名
        connectionFactory.setUsername("admin");
        //密码
        connectionFactory.setPassword("123456");
        //创建连接
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            //声明 接受消息
            DeliverCallback deliverCallback = (consumerTag, message) -> {
                System.out.println(new String(message.getBody()));
            };
            //取消信息时的回调
            CancelCallback cancelCallback = Consumer -> {
                System.out.println("消费消息被中断");
            };

            /**
             *消费者消费消息
             * 1.消费者队列
             * 2.消费成功后，是否要自动应答，true：自动应答，false：不自动应答
             * 3.消费者未成功消费的回调
             * 4.消费者取录消费的回调
             */
            channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
