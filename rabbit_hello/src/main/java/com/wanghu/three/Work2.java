package com.wanghu.three;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.wanghu.util.RabbitMqUtils;
import com.wanghu.util.sleep;

/**
 * @author wanghu
 * @date 2022/1/31 20:40
 *
 * 消息在手动应答时不丢失，放回队列中重新消费
 */
//消费者
public class Work2 {

    //队列的名称
    private static final String ACK_QUEUE_NAME="ack_queue";

    public static void main(String[] args) throws Exception {
        //接受消息
        Channel channel= RabbitMqUtils.getChannel();
        System.out.println("C1等待接收消息处理时间短");

        DeliverCallback deliverCallback=(consumerTag,message)->{
            //沉睡1秒
            sleep.sleep(1);
            System.out.println(" 接收到的消息："+new String(message.getBody()));
            /**
             * 1.消息的标记 tag
             * 2.是否批量应答 false：不批量，true：批量
             */
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
        };
//        //设置不公平分发
//        int prefetchCount=1;
//        channel.basicQos(prefetchCount);

        //设置预取值
        int prefetchCount=2;
        channel.basicQos(prefetchCount);

        //手动应答
        boolean autoAck=false;
        channel.basicConsume(ACK_QUEUE_NAME,autoAck,deliverCallback,(consumerTag)->{
            System.out.println(consumerTag+"消费者取消消费接口回调逻辑");
        });
    }
}
