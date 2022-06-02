package org.springcms.demo.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

@Component
@Description("监听队列")
public class RabbitEvent {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "topic.queue.error.integral"),
            exchange = @Exchange(value = "topic.queue.exchange.error", type = ExchangeTypes.TOPIC),
            key = "topic.queue.error.update"
    ))
    public void listen(String content) {
        logger.info(content);
    }
}
