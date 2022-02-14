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
public class Work3 {

    //队列的名称
    private static final String ACK_QUEUE_NAME="ack_queue";

    public static void main(String[] args) throws Exception {
        //接受消息
        Channel channel= RabbitMqUtils.getChannel();
        System.out.println("C2等待接收消息处理时间长");

        DeliverCallback deliverCallback=(consumerTag,message)->{
            //沉睡1秒
            sleep.sleep(10);
            System.out.println("接收到的消息："+new String(message.getBody()));
            /**
             * 1.消息的标记 tag
             * 2.是否批量应答 false：不批量，true：批量
             */
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
        };
//        //设置不公平分发
//        int prefetchCount=1;
//        channel.basicQos(prefetchCount);

        /**
         * 设置预取值
         * 这里的数字，是几，就会接收几条消息
         * 本身消息的发送就是异步发送的，所以在任何时候，channel 上肯定不止只有一个消息另外来自消费
         * 者的手动确认本质上也是异步的。因此这里就存在一个未确认的消息缓冲区，因此希望开发人员能限制此
         * 缓冲区的大小，以避免缓冲区里面无限制的未确认消息问题。这个时候就可以通过使用 basic.qos 方法设
         * 置“预取计数”值来完成的。该值定义通道上允许的未确认消息的最大数量。一旦数量达到配置的数量，
         * RabbitMQ 将停止在通道上传递更多消息，除非至少有一个未处理的消息被确认，例如，假设在通道上有
         * 未确认的消息 5、6、7，8，并且通道的预取计数设置为 4，此时 RabbitMQ 将不会在该通道上再传递任何
         * 消息，除非至少有一个未应答的消息被 ack。比方说 tag=6 这个消息刚刚被确认 ACK，RabbitMQ 将会感知
         * 这个情况到并再发送一条消息。消息应答和 QoS 预取值对用户吞吐量有重大影响。通常，增加预取将提高
         * 向消费者传递消息的速度。虽然自动应答传输消息速率是最佳的，但是，在这种情况下已传递但尚未处理
         * 的消息的数量也会增加，从而增加了消费者的 RAM 消耗(随机存取存储器)应该小心使用具有无限预处理
         * 的自动确认模式或手动确认模式，消费者消费了大量的消息如果没有确认的话，会导致消费者连接节点的
         * 内存消耗变大，所以找到合适的预取值是一个反复试验的过程，不同的负载该值取值也不同 100 到 300 范
         * 围内的值通常可提供最佳的吞吐量，并且不会给消费者带来太大的风险。预取值为 1 是最保守的。当然这
         * 将使吞吐量变得很低，特别是消费者连接延迟很严重的情况下，特别是在消费者连接等待时间较长的环境
         * 中。对于大多数应用来说，稍微高一点的值将是最佳的。
         */
        //设置预取值
        int prefetchCount=5;
        channel.basicQos(prefetchCount);

        //手动应答
        boolean autoAck=false;
        channel.basicConsume(ACK_QUEUE_NAME,autoAck,deliverCallback,(consumerTag)->{
            System.out.println(consumerTag+"消费者取消消费接口回调逻辑");
            System.out.println(channel.getChannelNumber());
        });
    }
}
