package org.springcms.rabbit.core.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springcms.rabbit.core.entity.MqProducer;
import org.springcms.rabbit.core.service.MqProducerService;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class RabbitConfirmCallback implements RabbitTemplate.ConfirmCallback {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected MqProducerService producerService;

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String uuid = correlationData.getId();
        if (ack) {
            logger.info(String.format("消息【%s】发送成功", uuid));
            producerService.updateQueueStatus(uuid, MqProducer.QueueStatue.QUEUE_STATUE_SENDED.ordinal());
        } else {
            logger.error(String.format("消息【%s】发送失败：%s", uuid, cause));
            producerService.updateQueueStatus(uuid, MqProducer.QueueStatue.QUEUE_STATUE_NOTSENT.ordinal());
        }
    }
}
