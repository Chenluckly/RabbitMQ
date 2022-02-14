package com.wanghu.four;

/**
 * @author wanghu
 * @date 2022/2/1 11:54
 */

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.wanghu.util.RabbitMqUtils;

import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 发布确认模式
 */
public class ConfirmMessage {

    //批量发消息的个数
    public static final int MESSAGE_COUNT = 1000;

    public static void main(String[] args) throws Exception {
        //1.单个确认
//        ConfirmMessage.PublishMessageIndividually();
        //2.批量确认
//        ConfirmMessage.PublishMessageBatch();
        //3.异步批量确认
        ConfirmMessage.PublishMessageAsync();
    }

    //单个发布确认
    public static void PublishMessageIndividually() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);
        //开启发布确认
        channel.confirmSelect();

        //开始时间
        long come = System.currentTimeMillis();

        //批量发消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());
            //单个消息就马上进行发布确认
            boolean b = channel.waitForConfirms();
            if (b) {
                System.out.println("消息发送成功：" + message);
            }

        }
        //结束时间
        long over = System.currentTimeMillis();
        System.out.println("发布：" + MESSAGE_COUNT + "个单独确认消息，耗时：" + (over - come) + "ms");

    }

    //批量发布确认
    public static void PublishMessageBatch() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);
        //开启发布确认
        channel.confirmSelect();

        //开始时间
        long come = System.currentTimeMillis();

        //批量确认消息大小
        int batchSize = 100;
        //未确认消息个数
        int outstandingMessageCount = 0;

        //批量发消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());
            outstandingMessageCount++;
            if (outstandingMessageCount == batchSize) {
                channel.waitForConfirms();
                outstandingMessageCount = 0;
            }
            //为了确保还有剩余没有确认消息 再次确认
            if (outstandingMessageCount > 0) {
                channel.waitForConfirms();
            }
        }
        //结束时间
        long over = System.currentTimeMillis();
        System.out.println("发布：" + MESSAGE_COUNT + "批量确认消息，耗时：" + (over - come) + "ms");
    }

    //异步发布确认
    public static void PublishMessageAsync() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);
        //开启发布确认
        channel.confirmSelect();
        //开始时间
        long come = System.currentTimeMillis();

        /**
         * 线程安全有序的一个哈希表，适用于高并发的情况
         * 1.轻松的将序号与消息进行关联
         * 2.轻松批量删除条目 只要给到序列号
         * 3.支持并发访问
         */
        ConcurrentSkipListMap<Long,String>outstandingConfirm=new ConcurrentSkipListMap<>();

        //消息确认成功，回调函数
        ConfirmCallback ackCallback=(deliverTag,multiple)->{
            if(multiple) {
                //2.删除到已经确认的消息，剩下的就是未确认的消息
                ConcurrentNavigableMap<Long, String> longStringConcurrentNavigableMap = outstandingConfirm.headMap(deliverTag);
                System.out.println("确认的消息：" + deliverTag);
                longStringConcurrentNavigableMap.clear();
            }else {
                outstandingConfirm.remove(deliverTag);
            }
            System.out.println("确认的消息："+deliverTag);
        };
        //消息确认失败，回调函数
        /**
         * 1.消息的标记
         * 2.是否未批量
         */
        ConfirmCallback nackCallback=(deliverTag,multiple)->{
            //3.打印一下未确认的消息都有哪些
            String s = outstandingConfirm.get(deliverTag);
            System.out.println("未确认的消息是："+s+"未确认的tag："+deliverTag);
        };
        /**
         * 消息的监听器,监听哪些消息成功了，哪些消息失败了
         * 1.ackCallback：监听哪些消息成功了
         * 2.nackCallback：监听哪些消息失败
         */
        channel.addConfirmListener(ackCallback,nackCallback);//异步通知

        //批量发布消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message="消息"+i;
            channel.basicPublish("",queueName,null,message.getBytes());
            //此处要记录下所以要发送的消息，消息的总和
            outstandingConfirm.put(channel.getNextPublishSeqNo(),message);
        }

        //结束时间
        long over = System.currentTimeMillis();
        System.out.println("发布：" + MESSAGE_COUNT + "异步确认消息，耗时：" + (over - come) + "ms");
    }
}
