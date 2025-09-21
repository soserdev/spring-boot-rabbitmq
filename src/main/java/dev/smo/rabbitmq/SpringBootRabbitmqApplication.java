package dev.smo.rabbitmq;

import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class SpringBootRabbitmqApplication {

	private static final Logger log = LoggerFactory.getLogger(SpringBootRabbitmqApplication.class);

	@Value("${rabbitmq.exchange.name}")
	private String exchangeName;

	@Value("${rabbitmq.queue.name}")
	private  String queueName;

	@Value("${rabbitmq.routing.key}")
	private String routingKeyName;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRabbitmqApplication.class, args);
	}

	@Bean
	public ApplicationRunner runner(RabbitTemplate template) {
		return args -> {
			log.info("Send message to Direct exchange...");

			var now = LocalDateTime.now();
			var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

			// For direct exchange use exchange name and routing key
			template.convertAndSend(exchangeName, routingKeyName, String.format("Message to Direct exchange: %s with routing key: %s at: %s", exchangeName, routingKeyName, now.format(formatter)));
		};
	}

	@RabbitListener(queues = "${rabbitmq.queue.name}")
	public void listen(String message) {
        log.info("Read message from direct exchange: {}", message);
	}

}
