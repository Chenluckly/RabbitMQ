package com.wanghu.three;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.wanghu.util.RabbitMqUtils;

import java.util.Scanner;

/**
 * @author wanghu
 * @date 2022/1/31 18:20
 */
//生产者
public class Tak2 {
    //队列的名称
    private static final String ACK_QUEUE_NAME="ack_queue";

    //发送大量消息
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //队列的声明
        /**
         * 生成一个队列
         * 1.队列名称
         * 2.队列里面的消息是否持久化（磁盘），默认情况存储在内存中
         * 3.该队列是否只服务一个消费者进行消费，是否进行消息共享，true：可以多个消费者，false：只能一个消费者消费
         * 4.是否自动删除，最后一个消费者断开连接以后，该队列是否自动删除，true：自动删除，false：不自动删除
         * 5.其他参数
         */
        /**
         * durable：持久化
         */
        boolean durable=true;
        channel.queueDeclare(ACK_QUEUE_NAME,durable,false,false,null);
        //从控制台当中接收信息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String message=scanner.next();
            //要想让消息实现持久化需要在消息生产者修改代码，MessageProperties.PERSISTENT_TEXT_PLAIN 添加这个属性。
            channel.basicPublish("",ACK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());
            System.out.println("生产者发出消息："+message);
        }
    }
}
