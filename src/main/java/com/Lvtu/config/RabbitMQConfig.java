package com.Lvtu.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.Lvtu.utils.RabbitMQConstants.*;

@Configuration
public class RabbitMQConfig {
    @Bean
    public DirectExchange blogNotificationExchange() {
        return new DirectExchange(BLOG_NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Queue blogNotificationQueue() {
        return new Queue(BLOG_NOTIFICATION_QUEUE);
    }

    @Bean
    public Binding blogNotificationBinding() {
        return BindingBuilder.bind(blogNotificationQueue())
                .to(blogNotificationExchange())
                .with(BLOG_NOTIFICATION_ROUTING_KEY);
    }
}
