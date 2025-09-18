# RabbitMQ

This is a Simple Demo WebApp that shows how to implement RabbitMQ using Spring Boot.

## About RabbitMQ and Asynchronous Messaging

**RabbitMQ** is an open-source message broker software that helps applications communicate with each other by sending
and receiving messages in a reliable, asynchronous, and scalable way. It acts as a middleman between different services
in a distributed system, enabling decoupling of components.

>_Asynchronous messaging_ is a way of indirectly sending messages 
from one application to another without waiting for a response. 
This indirection affords looser coupling and greater scalability 
between the communicating applications. _(Spring in Action 6th ed - Manning)_

As perhaps the most widely used implementation of the **Advanced Message Queuing Protocol (AMQP)**, RabbitMQ provides a more
flexible message-routing mechanism than **JMS** (**Java Messaging Protocol**). In JMS, messages are sent directly to a 
named destination, such as a **Queue** or a **Topic**, from which the receiver consumes them. 

In contrast, AMQP messages are published to an **Exchange** using a **Routing Key**, which is independent of the queues 
that receive the messages. This decouples message production from consumption, allowing for more advanced routing configurations.

## Components in RabbitMQ

Before getting into binding types, let's understand the basic components.

* **Exchange**: Receives messages from producers and routes them to queues based on rules called **bindings**.
* **Queue**: Stores messages until they are consumed.
* **Binding**: A link between an exchange and a queue, optionally including a **routing key**.
* **Routing Key**: A string used by the exchange to decide how to route the message.

## Start RabbitMQ

In order to install and start RabbitMQ, we use either **Docker** or **Docker Compose**.

```bash
docker run --hostname my-rabbit -p 8081:15672 -p 5671:5671 -p 5672:5672 -d rabbitmq:3.12-management
```

There is a `docker-compose.yml` file.

```bash
version: '3.8'
services:
  rabbitmq:
    image: rabbitmq:4.1.4-management-alpine
    container_name: rabbitmq
    ports:
      - "5672:5672"    # AMQP protocol port
      - "15672:15672"  # Management UI
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest
```

We start it using the following command.

```bash
docker-compose up -d
```

Now we can access the Management-Console [http://localhost:15672](http://localhost:15672). The default user and password are `guest` and `guest`.

In order to stop our RabbitMQ we use the following command.

```bash
docker-compose stop
```

## Getting Started

Before you can send or receive RabbitMQ messages with Spring Boot, you’ll need to add Spring Boot’s AMQP starter dependency.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

This dependency triggers autoconfiguration and creates an AMQP connection factory and RabbitTemplate beans.

In order to produce and consume a `Hello world!` message, we add the following lines to our application.

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

## Working with RabbitMQ

It is important to understand that messages are sent to exchanges using a routing key
and they’re consumed from a queue using a name. 

```java
public void convertAndSend(String exchange, String routingKey, Object object) throws AmqpException
```

How they get from an exchange to a queue depends on the type of exchange and the binding between the exchange and queues.

There a several kinds of bindings.

- **Direct** — Routes messages to a Queue with a **Binding Key** that matches exactly the messages **Routing Key**.
- **Topic** — Routes a message to one or more queues based upon **Binding Keys** that contain **wildcards** like `*` and `#`.
- **Fanout** — Broadcasts messages to all bound queues without regard for binding keys or routing keys.
- **Headers** — Routes messages based on the message headers.