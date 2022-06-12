package org.springcms.rabbit.core.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springcms.rabbit.core.entity.MqProducer;
import org.springcms.rabbit.core.service.MqProducerService;
import org.springcms.rabbit.core.utils.RabbitMqUtils;
import org.springframework.context.annotation.Description;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Description("检查消息队列中发送失败的记录")
public class ProducerQueueTask {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private RabbitMqUtils rabbitMqUtils;
    @Resource
    private MqProducerService producerService;

    /**
     * 每分钟检测一次发送失败的记录
     */
    @Scheduled(fixedDelay = 60 * 1000)
    public void execute() {
        List<MqProducer> producerList = producerService.getNotSentList(5);
        if (producerList == null && producerList.size() == 0) {
            return;
        }

        for (MqProducer producer : producerList) {
            rabbitMqUtils.push(producer);
        }
    }
}
