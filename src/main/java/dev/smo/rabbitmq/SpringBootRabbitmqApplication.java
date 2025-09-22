package dev.smo.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootRabbitmqApplication {

	private static final Logger log = LoggerFactory.getLogger(SpringBootRabbitmqApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRabbitmqApplication.class, args);
	}

	@RabbitListener(queues = "${rabbitmq.queue.name}")
	public void listen(String message) {
        log.info("RECEIVED MESSAGE: {}", message);
	}

}
