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

	@RabbitListener(queues = "${rabbitmq.queue.one}")
	public void listenQueueOne(String message) {
		// Receives all payment.* messages like payment.success
        log.info("QUEUE ONE - RECEIVED MESSAGE: {}", message);
	}

	@RabbitListener(queues = "${rabbitmq.queue.two}")
	public void listenQueueTwo(String message) {
		// Receives all #.error messages like payment.system.error
		log.info("QUEUE TWO - RECEIVED MESSAGE: {}", message);
	}
}
