package com.wanghu.Serven;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.wanghu.util.RabbitMqUtils;

/**
 * @author wanghu
 * @date 2022/2/1 19:51
 */
public class ReceiveLogsTopic01 {

    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME,"topic");
        String queueName="Q1";
        channel.queueDeclare(queueName,false,false,false,null);
        channel.queueBind(queueName,EXCHANGE_NAME,"*.orange.*");
        System.out.println("Q1等待接收消息");
        DeliverCallback deliverCallback=(consumerTag, message)->{
            System.out.println(" 接收到的消息："+new String(message.getBody())+"接收的队列："+queueName+"绑定键："+message.getEnvelope().getRoutingKey());
        };

        //接收消息
        channel.basicConsume(queueName,true,deliverCallback,consumerTag->{});



    }
}
