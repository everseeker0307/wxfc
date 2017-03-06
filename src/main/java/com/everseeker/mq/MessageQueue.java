package com.everseeker.mq;

import org.springframework.stereotype.Component;

import javax.jms.Queue;

/**
 * Created by everseeker on 2017/3/2.
 */
@Component
public interface MessageQueue {
    Queue queue();
}
