package io.eyce.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBootRabbitmqApplication {

	private static final String MY_QUEUE_NAME = "myQueue";
	private static final boolean DURABLE = true;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRabbitmqApplication.class, args);
	}


	@Bean
	public ApplicationRunner runner(RabbitTemplate template) {
		return args -> {
			System.out.println("Send message to queue");
			template.convertAndSend(MY_QUEUE_NAME, "Hello, world!");
		};
	}

	@Bean
	public Queue myQueue() {
		return new Queue(MY_QUEUE_NAME, DURABLE);
	}


	@RabbitListener(queues = MY_QUEUE_NAME)
	public void listen(String message) {
		System.out.println("Read message from queue: " + message);
	}

}
