package com.everseeker.mq;

import org.springframework.stereotype.Component;

/**
 * Created by everseeker on 2017/3/2.
 */
@Component
public interface MessageConsumer {
    void receive(Object obj);
}
