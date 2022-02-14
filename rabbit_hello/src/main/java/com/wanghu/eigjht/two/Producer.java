package com.wanghu.eigjht.two;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.wanghu.util.RabbitMqUtils;

/**
 * @author wanghu
 * @date 2022/2/1 21:56
 */

/**
 * 队列达到最大长度
 * 1. 消息生产者代码去掉 TTL 属性
 */
public class Producer {
    private static final String NORMAL_EXCHANGE = "normal_exchange";

    public static void main(String[] argv) throws Exception {
        try (Channel channel = RabbitMqUtils.getChannel()) {
            channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
//            //设置消息的 TTL 时间
//            AMQP.BasicProperties properties = new
//                    AMQP.BasicProperties().builder().expiration("5000").build();
            //该信息是用作演示队列个数限制
            for (int i = 1; i < 11; i++) {
                String message = "info" + i;
                channel.basicPublish(NORMAL_EXCHANGE, "zhangsan", null,
                        message.getBytes());
                System.out.println("生产者发送消息:" + message);
            }
        }
    }
}
