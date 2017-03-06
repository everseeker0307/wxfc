package com.everseeker.mq;

import com.everseeker.spider.Spider;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;

/**
 * Created by everseeker on 2017/3/2.
 */
@Component
public class ActiveMQConsumer implements MessageConsumer {
    @Autowired
    private Spider spider;

    @JmsListener(destination = "house-url", concurrency="5")
    public void receive(Object obj) {
        if (obj instanceof ActiveMQTextMessage) {
            ActiveMQTextMessage message = (ActiveMQTextMessage)obj;
            try {
                spider.getDetailPageAndSave(message.getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
