package dev.smo.rabbitmq.controller;

import dev.smo.rabbitmq.service.MessageProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/send")
public class SendMessageController {


    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Autowired
    MessageProducerService messageProducerService;

    @PostMapping("/{routingKey}")
    public void sendMessage(@PathVariable("routingKey") String routingKey, @RequestBody String message) {
        var now = LocalDateTime.now();
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        var messageToSend = String.format("Exchange: '%s' - Routing Key: '%s' - At: '%s' - Message: '%s'", exchangeName, routingKey, now.format(formatter), message);

        messageProducerService.send(exchangeName, routingKey, messageToSend);
    }
}
