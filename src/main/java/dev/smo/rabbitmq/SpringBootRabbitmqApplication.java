package dev.smo.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRabbitmqApplication.class, args);
	}

	@Bean
	public ApplicationRunner runner(RabbitTemplate template) {
		return args -> {
			var now = LocalDateTime.now();
			var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			var message = String.format("Topic exchange: '%s' - Queue name: '%s' - At: '%s'", exchangeName, queueName, now.format(formatter));
			// For topic exchange use queue name
			log.info("SENDING MESSAGE: " + message);
			template.convertAndSend(queueName, message);
		};
	}

	@RabbitListener(queues = "${rabbitmq.queue.name}")
	public void listen(String message) {
        log.info("RECEIVED MESSAGE: {}", message);
	}

}
