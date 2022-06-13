package org.springcms.rabbit.core.event;

import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public abstract class SingleMessageEvent extends AbstractMessageEvent {

    @Value("${rabbit.listener.sender}")
    private String sender;
    @Value("${rabbit.listener.receive}")
    private String receive;

    @Bean
    protected Queue queue() {
        return new Queue(String.format("topic.queue.%s.%s", sender, receive));
    }

    @Bean
    protected CustomExchange customExchange() {
        return new CustomExchange(String.format("topic.queue.exchange.%s", sender), String.format("topic.queue.%s.update", sender));
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @org.springframework.amqp.rabbit.annotation.Queue(value = "#{queue.name}"),
            exchange = @Exchange(value = "#{customExchange.name}", type = ExchangeTypes.TOPIC),
            key = "#{customExchange.type}"
    ))
    @RabbitHandler
    public void onMessage(String content) {
        super.onMessage(content);
    }

    public abstract void execute(String content) throws Exception;
}
