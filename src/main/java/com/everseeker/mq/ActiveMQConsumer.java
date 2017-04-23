package com.everseeker.mq;

import com.everseeker.spider.Spider;
import com.everseeker.utils.TimeUtil;
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

    /**
     * 设置消费者，并发线程数为10
     * @param obj
     */
    @JmsListener(destination = "house-url", concurrency="15")
    public void receive(Object obj) {
        if (obj instanceof ActiveMQTextMessage) {
            ActiveMQTextMessage message = (ActiveMQTextMessage)obj;
            try {
                if (message.getText().contains("queryFwmxInfo")) {
                    spider.updateSingleHouseTypeNum(message.getText());
                }
                else if (message.getText().contains("queryLpxxInfo")) {
                    spider.getDetailPageAndSave(message.getText());
                }
                TimeUtil.sleep(50);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
