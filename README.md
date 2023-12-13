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

