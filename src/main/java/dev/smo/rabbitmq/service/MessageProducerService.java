package dev.smo.rabbitmq.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class MessageProducerService {

    private static final Logger log = LoggerFactory.getLogger(MessageProducerService.class);

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    private final RabbitTemplate template;

    public MessageProducerService(RabbitTemplate template) {
        this.template = template;
    }

    public void send(String routingKey, String message) {
        log.info("SENDING MESSAGE: {}",  message);
        // For topic exchange use queue name
        template.convertAndSend(exchangeName, routingKey, message);
    }
}
