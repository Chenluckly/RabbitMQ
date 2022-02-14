package com.wanghu.two;

import com.rabbitmq.client.Channel;
import com.wanghu.util.RabbitMqUtils;

import java.util.Scanner;

/**
 * @author wanghu
 * @date 2022/1/31 18:20
 */
//生产者
public class Tak1 {
    //队列的名称
    private static final String QUEUE_NAME="hello";

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
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        //从控制台当中接收信息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String message=scanner.next();
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
            System.out.println("发送消息完成："+message);
        }

    }
}
