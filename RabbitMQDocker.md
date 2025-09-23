# RabbitMQ and Docker

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
