package dev.smo.rabbitmq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    @Value("${rabbitmq.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.queue.one}")
    private String queueNameOne;

    @Value("${rabbitmq.queue.two}")
    private String queueNameTwo;

    @Value("${rabbitmq.binding.one}")
    private String bindingNameOne;

    @Value("${rabbitmq.binding.two}")
    private String bindingNameTwo;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Queue queueOne() {
        return new Queue(queueNameOne);
    }

    @Bean
    public Queue queueTwo() {
        return new Queue(queueNameTwo);
    }

    @Bean
    public Binding bindingOne(Queue queueOne) {
        log.info("CREATED BINDING for QUEUE ONE '{}' to '{}' with binding key '{}'", queueNameOne, exchangeName, bindingNameOne);
        return BindingBuilder.bind(queueOne).to(exchange()).with(bindingNameOne);
    }

    @Bean
    public Binding bindingTwo(Queue queueTwo) {
        log.info("CREATED BINDING for QUEUE TWO '{}' to '{}' with binding key '{}'", queueNameTwo, exchangeName, bindingNameTwo);
        return BindingBuilder.bind(queueTwo).to(exchange()).with(bindingNameTwo);
    }

}
