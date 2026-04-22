package com.example.takeway.firstspringboot.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {
    public static final String ORDER_QUEUE = "order.queue";             // 信箱名
    public static final String ORDER_EXCHANGE = "order.exchange";       // 分拣中心名
    public static final String ORDER_ROUTING_KEY = "order.create";// 包裹标签（路由键）

    @Bean
    public Queue orderQueue() {
        // true 代表持久化：就算 RabbitMQ 服务器断电重启，信箱里的订单也不会丢！
        return new Queue(ORDER_QUEUE, true);
    }

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
    }

    //告诉分拣中心 (Exchange)：只要看到包裹上贴着 "order.create" 的标签 (RoutingKey)，
    // 就毫不犹豫地把它扔进外卖信箱 (Queue) 里！
    @Bean
    public Binding orderBinding(Queue orderQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(ORDER_ROUTING_KEY);
    }

    //🚨 极其关键的新增配置：告诉邮局，所有包裹全部用 JSON 格式打包和拆包！
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
