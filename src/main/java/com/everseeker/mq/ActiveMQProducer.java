package com.everseeker.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Queue;

/**
 * Created by everseeker on 2017/3/2.
 */
@Component
public class ActiveMQProducer implements MessageProducer {
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;      //使用JmsMessagingTemplate将消息放入队列

    @Autowired
    private Queue queue;

    public void send(Object obj) {
        this.jmsMessagingTemplate.convertAndSend(this.queue, obj);
    }
}
