package com.wanghu.one;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author wanghu
 * @date 2022/1/31 14:39
 */
//生产者
public class Provide {
    //队列的名称
    private static final String QUEUE_NAME="hello";

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
    try{
        Connection connection = connectionFactory.newConnection();
        //获取信道
        Channel channel = connection.createChannel();
        /**
         * 生成一个队列
         * 1.队列名称
         * 2.队列里面的消息是否持久化（磁盘），默认情况存储在内存中
         * 3.该队列是否只服务一个消费者进行消费，是否进行消息共享，true：可以多个消费者，false：只能一个消费者消费
         * 4.是否自动删除，最后一个消费者断开连接以后，该队列是否自动删除，true：自动删除，false：不自动删除
         * 5.其他参数
         */
        Map<String,Object> arguments=new HashMap<>();
        arguments.put("x-max-priority",10);
         channel.queueDeclare(QUEUE_NAME,false,false,false,arguments);
        //发消息
        for (int i = 0; i <11 ; i++) {
            String message="info"+i;
            if(i==5){
                AMQP.BasicProperties amqp = new AMQP.BasicProperties().builder().priority(5).build();
                channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
            }

        }
        System.out.println("消息发送完毕");

         /**
          * 发送一个消费
          * 发送到那个交换机
          */
    } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }


    }

}
