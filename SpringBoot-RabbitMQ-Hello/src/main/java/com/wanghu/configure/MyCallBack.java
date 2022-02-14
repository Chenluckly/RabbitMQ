package com.wanghu.configure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author wanghu
 * @date 2022/2/2 21:48
 */
    @Component
    @Slf4j
    public class MyCallBack implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnsCallback{

        @Autowired
        private RabbitTemplate rabbitTemplate;

        @PostConstruct
        public void init(){
            //注入
            rabbitTemplate.setConfirmCallback(this);
            rabbitTemplate.setReturnsCallback(this);
        }


        /**
         交换机确认回调方法
         1.发消息交换机接收到了回调
         1.1 correlationData 保存回调消息的ID及相关信息
         1.2交换机收到消息ack = true
         1.3 cause null
         2.发消息交换机接收失败了回调
         2.1 correlationData保存回调消息的ID及相关信息2.2交换机收到消息ack = false
         2.3 cause失败的原因

         */
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            String id=correlationData!=null?correlationData.getId():"";
            if(ack){
                log.info("交换机已经收到 id 为:{}的消息",id);
            }else{
                log.info("交换机还未收到 id 为:{}消息,由于原因:{}",id,cause);
            }
        }


    //当消息无法路由的时候的回调方法
    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        log.error(returnedMessage.toString());
    }
}
