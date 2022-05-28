package org.springcms.utils;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springcms.vo.QueueVO;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    /**
     * 向RabbitMQ发送消息
     * @param source
     * @param content 消息内容
     */
    public void send(String source, String content) {
        String uuid = UUID.randomUUID().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        QueueVO queue = new QueueVO();
        queue.setUuid(uuid);
        queue.setSource(source.toLowerCase());
        queue.setContent(content);
        queue.setSendTime(sdf.format(new Date()));
        queue.setStatue(QueueVO.QueueStatue.QUEUE_STATUE_NOTSENT.ordinal());

        this.send(queue);
    }

    /**
     * 向RabbitMQ发送消息
     * @param queue
     */
    public void send(QueueVO queue) {
        CorrelationData correlationId = new CorrelationData(queue.getUuid());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", queue.getContent());
        jsonObject.put("uuid", queue.getUuid());

        rabbitTemplate.setConfirmCallback(this);
        try {
            rabbitTemplate.convertAndSend("topic_queue_exchange_".concat(queue.getSource()), String.format("topic.queue.%s.update", queue.getSource()), jsonObject.toString(), correlationId);
        } catch (Exception e) {
            logger.error(String.format("消息【%s】发送异常，%s", queue.getUuid(), e.getMessage()));
        }
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String uuid = correlationData.getId();
        if (ack) {
            //发送成功
            logger.debug(String.format("消息【%s】发送成功", uuid));
        } else {
            logger.error(String.format("消息【%s】发送失败", uuid));
        }
    }

}
