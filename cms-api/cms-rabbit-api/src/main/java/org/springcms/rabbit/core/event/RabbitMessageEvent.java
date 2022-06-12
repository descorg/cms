package org.springcms.rabbit.core.event;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springcms.rabbit.core.entity.MqConsumer;
import org.springcms.rabbit.core.service.MqConsumerService;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class RabbitMessageEvent<T> {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private MqConsumer consumer;

    @Resource
    private MqConsumerService consumerService;

    public void onMessage(String content) {
        logger.info("收到消息: {}", content);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject jsonObject = JSONObject.parseObject(content);

        LambdaQueryWrapper<MqConsumer> lqw = new LambdaQueryWrapper<>();
        lqw.eq(MqConsumer::getUuid, jsonObject.getString("uuid"));
        consumer = consumerService.getOne(lqw);

        if (consumer == null) {
            consumer = new MqConsumer();
            consumer.setUuid(jsonObject.getString("uuid"));
            consumer.setContent(jsonObject.getString("data"));
            consumer.setSource("error");
            consumer.setReceiver("integral");
            consumer.setRecvTime(sdf.format(new Date()));
            consumer.setStatus(MqConsumer.QueueStatue.QUEUE_STATUE_UNTREATED.ordinal());
            consumerService.save(consumer);
        }

        if (this.onBefore()) {
            try {
                this.execute(consumer.getContent());
                this.onSuccess();
            } catch (Exception e) {
                this.onError(e.getLocalizedMessage());
            }
        }
    }

    public abstract void execute(String content);

    /**
     * 开始执行前
     * @return 是否继续执行后面的任务
     */
    private boolean onBefore() {
        logger.info(String.format("%s onBefore", this.getClass().getName()));
        consumerService.updateQueueStatus(consumer.getUuid(), MqConsumer.QueueStatue.QUEUE_STATUE_PROCESSING.ordinal());
        return true;
    }

    /**
     * 执行成功
     */
    private void onSuccess() {
        logger.info(String.format("%s onSuccess", this.getClass().getName()));
        consumerService.updateQueueStatus(consumer.getUuid(), MqConsumer.QueueStatue.QUEUE_STATUE_UNTREATED.ordinal());
    }

    /**
     * 执行失败
     * @param message
     */
    private void onError(String message) {
        logger.info(String.format("%s onError:%s", this.getClass().getName(), message));
        consumerService.updateQueueStatus(consumer.getUuid(), MqConsumer.QueueStatue.QUEUE_STATUE_ALREADY.ordinal());
    }
}
