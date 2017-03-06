package com.everseeker.mq;

import org.springframework.stereotype.Component;

/**
 * Created by everseeker on 2017/3/2.
 */
@Component
public interface MessageProducer {
    //消息生产者，发送消息到队列中
    void send(Object obj);
}
