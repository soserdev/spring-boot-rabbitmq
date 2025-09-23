# RabbitMQ

This is a Simple Demo WebApp that shows how to implement RabbitMQ using Spring Boot.


## About RabbitMQ and Asynchronous Messaging

**RabbitMQ** is an open-source message broker software that helps applications communicate with each other by sending
and receiving messages in a reliable, asynchronous, and scalable way. _It works like a Post Office in the Cloud - see [RabbitMQ in 100 Seconds](https://www.youtube.com/watch?v=NQ3fZtyXji0)_. It acts as a middleman between different services
in a distributed system, enabling decoupling of components.

>_Asynchronous messaging_ is a way of indirectly sending messages 
from one application to another without waiting for a response. 
This indirection affords looser coupling and greater scalability 
between the communicating applications. _(Spring in Action 6th ed - Manning)_

## How it works

As perhaps the most widely used implementation of the **Advanced Message Queuing Protocol (AMQP)**, RabbitMQ provides a more
flexible message-routing mechanism than **JMS** (**Java Messaging Protocol**). In JMS, messages are sent directly to a 
named destination, such as a **Queue** or a **Topic**, from which the receiver consumes them. 

In contrast, AMQP messages are published to an **Exchange** using a **Routing Key**, which is independent of the queues 
that receive the messages. This decouples message production from consumption, allowing for more advanced routing configurations.

## Components in RabbitMQ

Before getting into binding types, let's understand the basic components.

* **Producer** sends messages to an **Exchange**.
* **Exchange**: Receives messages from producers and routes them to queues based on rules called **bindings**.
* **Consumer**: The message sits in the Queue until it is handled by a **Consumer**.
* **Queue**: Stores messages until they are consumed by a **Consumer**.
* **Binding**: A link between an exchange and a queue, optionally including a **routing key** (**binding key**).
* **Routing Key**: A string used by the exchange to decide how to route the message.

## Binding Types

There a several kinds of **Binding Types**.

- **Default**
- **Direct** — Routes messages to a specific Queue with a **Binding Key** that matches exactly the messages **Routing Key**.
- **Topic** — Routes a message to one or more queues based upon **Binding Keys** that contain a **pattern** which are **wildcards** like `*` and `#`.
- **Fanout** — Broadcasts messages to all bound queues without regard for binding keys or routing keys.
- **Headers** — Routes messages based on the message headers.


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

## A Simple Producer Consumer Example using the _Default Exchange_

In order to produce and consume a `Hello world!` message, we create a _Simple Producer and Consumer_ using the 
_Default Binding Type_ - see branch [simple-producer-consumer](https://github.com/soserdev/spring-boot-rabbitmq/tree/simple-producer-consumer).
We add the following lines to our application.

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

This sends a message `"Hello, world!"` to the queue `MY_QUEUE_NAME = "myQueue"` 
using RabbitTemplate in `template.convertAndSend(MY_QUEUE_NAME, "Hello, world!")`.

We configure a `@RabbitListener(queues = MY_QUEUE_NAME)` which consumes all messages sent to this Queue.

## A more complex Example using Topic Exchange

In RabbitMQ, a **Topic Exchange** routes messages to one or many queues **based on pattern matching** 
between the **routing key** and the **binding key**. 

This allows for **flexible routing** – more powerful than the simple direct or fanout exchanges. 
It’s powerful for **selective routing**, like logging, notifications, or filtering events.

Use `*` to match **one word**, and `#` to match **multiple words**. 

- `payment.*` matches `payment.received`, but it does not match `payment.system.error`. 
- `#.error` matches `authentication.error` and `payment.system.error`.

## Application properties

In order to configure multiple queues we use a configuration file.

```
# Exchange, Queue, and Routing Key
rabbitmq.exchange=topic.exchange
rabbitmq.queue.one=topic.queue.one
rabbitmq.queue.two=topic.queue.two
rabbitmq.binding.one=payment.*
rabbitmq.binding.two=#.error
rabbitmq.routing.one=topic.routing.one
rabbitmq.routing.two=topic.routing.two
```

## Configure Exchange, Queues, and Bindings

Now we configure the Exchange, Queues, and Bindings.

```java
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
```

## Working with RabbitMQ

In order to understand how we send a message to Topic, it is important to understand that messages are sent to exchanges using a routing key
and they’re consumed from a queue using a name. 

```java
public void convertAndSend(String exchange, String routingKey, Object object) throws AmqpException
```

How they get from an exchange to a queue depends on the type of exchange and the binding between the exchange and queues.

Now, we create a `MessageProducerService` to send messages to our Exchange.

```java
@Service
public class MessageProducerService {

    private static final Logger log = LoggerFactory.getLogger(MessageProducerService.class);

    private final RabbitTemplate template;

    public MessageProducerService(RabbitTemplate template) {
        this.template = template;
    }

    public void send(String exchangeName, String routingKey, String message) {
        log.info("SENDING MESSAGE: {}",  message);
        // For topic exchange use queue name
        template.convertAndSend(exchangeName, routingKey, message);
    }
}
```

## Receiving Messages for two Queues

In order to receive the messages for both queues, we have to update our main application.

```java
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
```

## Rest Api

In order to send messages to our queues, we create a simple REST Controller.

```java
@RestController
@RequestMapping("/api/v1/send")
public class SendMessageController {


    @Value("${rabbitmq.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.routing.one}")
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
```
## Example Routing Keys

Here are some examples for routing keys and the queues that receive the message.

| Message Sent With Routing Key | Queue.one Receives? | Queue.two Receives? |
|-------------------------------|---------------------|---------------------|
| `payment.success`             | ✅ Yes (`payment.*`) | ❌ No (`#.error`)   |
| `payment.error`               | ✅ Yes               | ✅ Yes               |
| `payment.system.error`        | ❌ No                | ✅ Yes               |



## Rest API Usage

Additionally this sample app offers a RESTful API to send messages to our two Consumers.

Since we use `payment.success` as a routing key, only **Queue One** will receive this message.

```bash
curl -X POST http://localhost:8080/api/v1/send/payment.success \
     -H "Content-Type: text/plain" \
     -d "Payment with id:123 success"
```

If we use `payment.error` as a routing key, both Queues **Queue One** and **Queue Two** will receive this message.

```bash
curl -X POST http://localhost:8080/api/v1/send/payment.error \
-H "Content-Type: text/plain" \
-d "Payment with id:123 error"
```

And if we use `payment.error` as a routing key, only **Queue Two** will receive the message.

```bash
curl -X POST http://localhost:8080/api/v1/send/payment.system.error \
     -H "Content-Type: text/plain" \
     -d "Payment System  error"
```

---

## Docker Volumes

If you don't configure a volume, RabbitMQ does not save the data in a volume. Consider the following configurataion.

```
version: '3.8'

services:
  rabbitmq:
    image: rabbitmq:4.0.9-management-alpine
    container_name: rabbitmq
    ports:
      - "5672:5672"     # AMQP
      - "15672:15672"   # Management UI
    volumes:
      - rabbitmq_data:/var/lib/rabbitmq
volumes:
  rabbitmq_data:
```

When you use a named volume like `rabbitmq_data` in Docker, Docker stores the data on the host machine, 
but the exact location depends on your OS and Docker storage driver.

### Typical Location

If you're running Docker on **Linux**, Docker volumes are usually stored under:

```
/var/lib/docker/volumes/rabbitmq_data/_data/
```

Docker on macOS and Windows runs inside a lightweight Linux VM, so the files **aren’t directly stored on your native filesystem**. Instead, they are inside that VM, and **not easily accessible** unless you use Docker commands or volume mounting.

###  How to Inspect Volumes

To check where the volume is stored, run:

```bash
docker volume inspect rabbitmq_data
```

### How to Remove the volume

If you ever want to remove the data (⚠️ this will delete all RabbitMQ queues, messages, etc):

```bash
docker volume rm rabbitmq_data
```

