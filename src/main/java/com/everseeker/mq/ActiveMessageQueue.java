package com.everseeker.mq;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.jms.Queue;

/**
 * Created by everseeker on 2017/3/2.
 */
@Component
public class ActiveMessageQueue implements MessageQueue {

    @Bean
    public Queue queue() {
        return new ActiveMQQueue("house-url");
    }
}
