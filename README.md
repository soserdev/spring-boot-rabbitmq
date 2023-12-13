# RabbitMQ

Simple RabbitMQ Demo Webapp.

## Start RabbitMQ

In order to start RabbitMQ, we use either _docker_ or docker-compose.

```bash
docker run --hostname my-rabbit -p 8081:15672 -p 5671:5671 -p 5672:5672 -d rabbitmq:3.12-management
```

There is a `docker-compose.yml` file.

```bash
version: '2'
services:
  rabbitmq:
    hostname: my-rabbit
    image: rabbitmq:3.12-management
    ports:
      - "5672:5672"
      - "15672:15672"
```

We start it using the following command.

```bash
docker-compose up -d
```

In order to stop it we use the `docker-compose stop` command.

## Produce and Consume

Now we produce and consume a `Hello world!` message.

```bash
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
```