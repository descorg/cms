package org.springcms.rabbit.utils;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springcms.rabbit.event.SendStateListener;
import org.springcms.vo.QueueVO;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
@Service
public class RabbitUtils implements RabbitTemplate.ConfirmCallback {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitAdmin rabbitAdmin;

    private SendStateListener sendStateListener;

    /**
     * 向RabbitMQ发送消息
     * @param source
     * @param content 消息内容
     */
    public void send(String source, String content, SendStateListener sendStateListener) {
        String uuid = UUID.randomUUID().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        QueueVO queue = new QueueVO();
        queue.setUuid(uuid);
        queue.setSource(source.toLowerCase());
        queue.setContent(content);
        queue.setSendTime(sdf.format(new Date()));
        queue.setStatue(QueueVO.QueueStatue.QUEUE_STATUE_NOTSENT.ordinal());

        this.send(queue, sendStateListener);
    }

    /**
     * 向RabbitMQ发送消息
     * @param queue
     * @param sendStateListener
     */
    public void send(QueueVO queue, SendStateListener sendStateListener) {
        this.sendStateListener = sendStateListener;
        if (this.sendStateListener != null) {
            this.sendStateListener.onBefore(queue);
        }

        CorrelationData correlationId = new CorrelationData(queue.getUuid());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", queue.getContent());
        jsonObject.put("uuid", queue.getUuid());

        rabbitTemplate.setConfirmCallback(this);
        try {
            rabbitTemplate.convertAndSend("topic.queue.exchange.".concat(queue.getSource()), String.format("topic.queue.%s.update", queue.getSource()), jsonObject.toString(), correlationId);
        } catch (Exception e) {
            logger.error(String.format("消息【%s】发送异常，%s", queue.getUuid(), e.getMessage()));
            if (this.sendStateListener != null) {
                this.sendStateListener.onError(queue.getUuid(), e.getMessage());
            }
        }
    }

    /**
     * 动态创建队列，并绑定到交换机
     * @param queueName 队列简称
     */
    public void create(String queueName) {
        Queue queue = new Queue(String.format("topic.queue.%s", queueName.toLowerCase()));
        TopicExchange exchange = new TopicExchange(String.format("topic.queue.exchange.%s", queueName.toLowerCase()));
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(String.format("topic.queue.%s.#", queueName.toLowerCase())));
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String uuid = correlationData.getId();
        if (ack) {
            //发送成功
            logger.info(String.format("消息【%s】发送成功", uuid));
            if (sendStateListener != null) {
                sendStateListener.onComplete(uuid);
            }
        } else {
            logger.error(String.format("消息【%s】发送失败：%s", uuid, cause));
            if (sendStateListener != null) {
                sendStateListener.onError(uuid, cause);
            }
        }
    }

}
