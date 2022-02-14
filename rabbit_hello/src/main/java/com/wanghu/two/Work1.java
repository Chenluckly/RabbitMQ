package com.wanghu.two;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.wanghu.util.RabbitMqUtils;
/**
 * @author wanghu
 * @date 2022/1/31 17:25
 */
//消费者
public class Work1 {
    //队列的名称
    private static final String QUEUE_NAME="hello";

    //接受消息
    public static void main(String[] args) throws Exception {
        Channel channel= RabbitMqUtils.getChannel();
        //消息的接收
        DeliverCallback deliverCallback=(consumerTag,message)->{
            System.out.println("接收到的消息："+new String(message.getBody()));
        };
        //消息接收被取消时，执行下面的内容
        CancelCallback cancelCallback=(consumerTag)->{
            System.out.println(consumerTag+"消费者取消消费接口回调逻辑");
        };
        System.out.println("A等待接收消息");
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);
    }
}
