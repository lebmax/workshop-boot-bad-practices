# Проект получения информации по имени человека

Краткое описание проекта:

* REST-сервис с UI-страницей, доступной на [localhost:8080](http://127.0.0.1:8080)
* Задача — предоставить информацию по имени (предположительный пол и возраст)
* При формировании ответа обращается к другим сервисам
* Нефункциональные требования — количество запросов с одного IP должно быть ограничено (rate-limit)

# Структура
Проект состоит из двух модулей:

* [workshop-app](workshop-app) — само приложение с REST-сервисом и UI-страницей
* [rate-limiter-starter](rate-limiter-starter) — наш стартер, который используется в приложении и каких-то других наших проектах

## workshop-app (Практика, часть 1)

Модуль самого приложения с эндпойнтом для предоставления информации о запрошенном имени.  
Для тестирования можно воспользоваться графическим интерфейсом [localhost:8080](http://127.0.0.1:8080) либо вызывать `api/names/{name}/info` самостоятельно.  
Попробуйте найти максимальное количество отклонений от Best Practices в данном модуле.  
Используемый в проекте [rate-limiter-starter](rate-limiter-starter) пока не рассматривать  
Для удобства найденные отклонения можно перечислить прямо здесь:
1. ...
2. ...


## rate-limiter-starter (Практика, часть 2)

Наш собственный **стартер** для ограничения числа запросов (rate-limit).  
Подключается в модуль приложения [workshop-app](workshop-app).  
При подключении стартера, можно помечать методы аннотацией [@RateLimiter](rate-limiter-starter/src/main/java/ru/yandex/workshop/ratelimiter/annotation/RateLimited.java).
Тем самым создадутся прокси для реализации АОП, вокруг которых будет срабатывать [RateLimiter](rate-limiter-starter/src/main/java/ru/yandex/workshop/ratelimiter/core/RateLimiterInMemoryImpl.java)  
Попробуйте сделать несколько запросов подряд и убедиться что рейт-лимитер не срабатывает.  
Необходимо разобраться в причинах (подсказка — конфигурации стартера и пропертей) и поправить стартер так, чтобы он работал

# Kafka Avro Producer-Consumer Example

This project demonstrates a Spring Boot application with Kafka producer and consumer using Avro serialization.

## Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- Gradle

## Setup

1. Start Kafka and Schema Registry using Docker Compose:

```yaml
version: '3'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.6.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:7.6.0
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  schema-registry:
    image: confluentinc/cp-schema-registry:7.6.0
    depends_on:
      - kafka
    ports:
      - "8081:8081"
    environment:
      SCHEMA_REGISTRY_HOST_NAME: schema-registry
      SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS: kafka:29092
      SCHEMA_REGISTRY_LISTENERS: http://0.0.0.0:8081
```

Save this as `docker-compose.yml` and run:

```bash
docker-compose up -d
```

2. Build the project:

```bash
./gradlew clean build
```

## Running the Applications

1. Start the Producer (runs on port 8090):

```bash
./gradlew :kafka-producer:bootRun
```

2. Start the Consumer (runs on port 8091):

```bash
./gradlew :kafka-consumer:bootRun
```

## How it Works

- The Producer generates a message every 5 seconds and sends it to the "avro-messages" topic
- The Consumer reads messages from the topic and prints them to the console
- Messages are serialized/deserialized using Avro schema
- The Consumer manually commits offsets after processing each batch of messages

## Monitoring

You can monitor the Kafka topics and messages using Confluent Control Center or other Kafka management tools.

## Stopping the Applications

1. Stop the Spring Boot applications using Ctrl+C
2. Stop the Docker containers:

```bash
docker-compose down
```

