# Kafka Microservices Demo

This project demonstrates a simple microservices setup with **Spring Boot**, **Apache Kafka**, and **Zookeeper** running inside **Docker Compose**. All services are custom-built from source code in this project.

## 🚀 Project Structure

```
├── main/                     # Main service (Spring Boot app)
│   ├── Dockerfile
│   └── src/
├── kafka/                    # Custom Kafka service
│   ├── Dockerfile
│   └── src/
├── discovery/                # Zookeeper service build context
│   ├── Dockerfile
│   └── src/
├── docker-compose.yml        # Orchestration of all services
└── README.md
```

## 🛠️ Services

- **Zookeeper (`zookeeper-service`)**
  - Built from custom Dockerfile under `./discovery`
  - Exposed on port **2181**
  
- **Kafka (`kafka`)**
  - Built from custom Dockerfile under `./kafka`
  - Uses Apache Kafka 3.8.0 with Scala 2.13
  - Connected to `zookeeper-service:2181`
  - Exposed on port **9092**
  
- **Main Service (`main-service`)**
  - Spring Boot service packaged as a JAR
  - Talks to Kafka (`kafka:9092`) and Zookeeper (`zookeeper-service:2181`)
  - Exposed on port **8080**

## ⚙️ Configuration

### Main Service (`application.properties`)

```properties
spring.application.name=main-service
server.port=8080

# Kafka
spring.kafka.bootstrap-servers=kafka:9092

# Zookeeper
spring.cloud.zookeeper.connect-string=zookeeper-service:2181

# Mail (example)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<your_email>
spring.mail.password=<app_password>
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Discovery Service (`application.properties`)

```properties
spring.application.name=discovery-service
spring.cloud.zookeeper.connect-string=zookeeper-service:2181
spring.cloud.zookeeper.discovery.enabled=true
```

### Kafka Service (`application.properties`)

```properties
spring.application.name=kafka-service

spring.kafka.bootstrap-servers=kafka:9092
spring.kafka.consumer.group-id=demo-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer
```

### Kafka Dockerfile Configuration

The custom Kafka service is configured with:
- **Broker ID**: 1
- **Listeners**: `PLAINTEXT://0.0.0.0:9092`
- **Advertised Listeners**: `PLAINTEXT://kafka:9092`
- **Zookeeper Connect**: `zookeeper-service:2181`
- **Replication Factor**: 1 (single-broker setup)

## ▶️ Running the Project

1. Build and start all containers:

   ```bash
   docker compose up --build
   ```

2. Verify running services:

   ```bash
   docker ps
   ```

   Expected:
    * `zookeeper-service` on **2181**
    * `kafka` on **9092**
    * `main-service` on **8080**

3. Access **Main Service**:

   ```
   http://localhost:8080
   ```

## 📡 Networking

All services communicate on the same Docker network:

```
main-service ──> kafka:9092 ──> zookeeper-service:2181
                   ↑
                   │
             kafka-service
```

## 🧪 Testing

* **Produce a message** manually from inside the Kafka container:

  ```bash
  docker exec -it kafka bash
  cd /opt/kafka
  bin/kafka-console-producer.sh --broker-list kafka:9092 --topic demo-topic
  ```

  Then type a message and hit **Enter**.

* **Consume messages**:

  ```bash
  docker exec -it kafka bash
  cd /opt/kafka
  bin/kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic demo-topic --from-beginning
  ```

* **Check logs**:

  ```bash
  docker compose logs -f kafka
  docker compose logs -f main-service
  ```

  to see message flow and service behavior.

* **Create a topic** (optional):

  ```bash
  docker exec -it kafka bash
  cd /opt/kafka
  bin/kafka-topics.sh --create --topic demo-topic --bootstrap-server kafka:9092 --partitions 1 --replication-factor 1
  ```

## ✅ Notes

* All services (Zookeeper, Kafka, Main) are **custom-built from this project—**no external images are used
* Replace mail credentials with your own before running
* Ensure Spring Boot & Spring Cloud versions are compatible:
    * Spring Boot **3.2.x / 3.3.x**
    * Spring Cloud **2023.x**
* Do **not** use `localhost` inside container configs; always use the **service name** (`kafka`, `zookeeper-service`, etc.)
* Kafka service uses **Apache Kafka 3.8.0** running on **Eclipse Temurin JDK 17**

## 🔧 Docker Compose Configuration

```yaml
version: '3.8'

services:
  zookeeper:
    build:
      context: ./discovery
    container_name: zookeeper-service
    ports:
      - "2181:2181"

  kafka:
    build:
      context: ./kafka
      dockerfile: Dockerfile
    container_name: kafka
    ports:
      - "9092:9092"
    depends_on:
      - zookeeper

  main-service:
    build:
      context: ./main
      dockerfile: Dockerfile
    container_name: main-service
    ports:
      - "8080:8080"
    environment:
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
    depends_on:
      - kafka
```

---

✨ Happy coding with your custom Kafka & Spring Boot microservices!
```