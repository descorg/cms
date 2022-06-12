package org.springcms.rabbit.core.utils;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springcms.rabbit.core.entity.MqProducer;
import org.springcms.rabbit.core.event.RabbitConfirmCallback;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
public class RabbitMqUtils extends RabbitConfirmCallback {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 动态创建队列，并绑定到交换机
     * @param queueName 队列简称
     */
    public void create(String queueName) {
        Queue queue = new Queue(String.format("topic.queue.%s", queueName.toLowerCase()));
        TopicExchange exchange = new TopicExchange(String.format("topic.queue.exchange.%s", queueName.toLowerCase()));

        RabbitAdmin rabbitAdmin = (RabbitAdmin)applicationContext.getBean("rabbitAdmin");
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(String.format("topic.queue.%s.#", queueName.toLowerCase())));
    }

    /**
     * 向RabbitMQ发送消息
     * @param source
     * @param content 消息内容
     */
    public void push(String source, String content) {
        String uuid = UUID.randomUUID().toString();
        MqProducer queue = new MqProducer();
        queue.setSource(source.toLowerCase());
        queue.setContent(content);
        queue.setUuid(uuid);

        this.push(queue);
    }

    /**
     * 向RabbitMQ发送消息
     * @param queue
     */
    public void push(MqProducer queue) {
        //发送前
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        queue.setSendTime(sdf.format(new Date()));
        queue.setStatus(MqProducer.QueueStatue.QUEUE_STATUE_SENDING.ordinal());
        producerService.save(queue);

        CorrelationData correlationId = new CorrelationData(queue.getUuid());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", queue.getContent());
        jsonObject.put("uuid", queue.getUuid());

        rabbitTemplate.setConfirmCallback(this);
        try {
            rabbitTemplate.convertAndSend("topic.queue.exchange.".concat(queue.getSource()), String.format("topic.queue.%s.update", queue.getSource()), jsonObject.toString(), correlationId);
        } catch (Exception e) {
            logger.error(String.format("消息【%s】发送异常，%s", queue.getUuid(), e.getMessage()));
            producerService.updateQueueStatus(queue.getUuid(), MqProducer.QueueStatue.QUEUE_STATUE_NOTSENT.ordinal());
        }
    }
}
