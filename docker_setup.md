# 🐳 Docker Compose - Инфраструктура TMS

## Что внутри?

- **Redis** (порт 6379) - кэширование
- **Kafka** (порт 9092) - event-driven архитектура
- **Mailhog** (порт 8025) - тестирование email
- **Prometheus** (порт 9090) - метрики
- **Grafana** (порт 3000) - визуализация метрик

**PostgreSQL**  запущен отдельно.

---

## 🚀 Быстрый старт

### 1. Запуск всех сервисов

```bash
docker-compose up -d
```

### 2. Проверка статуса

```bash
docker-compose ps
```

Должно быть всё в статусе `Up`:
```
NAME                IMAGE                              STATUS
tms-redis           redis:7-alpine                     Up (healthy)
tms-kafka           confluentinc/cp-kafka:7.5.0        Up (healthy)
tms-mailhog         mailhog/mailhog:latest             Up
tms-prometheus      prom/prometheus:latest             Up
tms-grafana         grafana/grafana:latest             Up
```

### 3. Проверка логов

```bash
# Все сервисы
docker-compose logs -f

# Конкретный сервис
docker-compose logs -f redis
docker-compose logs -f kafka
```

---

## 🔍 Доступ к сервисам

### Redis
```bash
# Подключение через CLI
docker exec -it tms-redis redis-cli

# Проверка
127.0.0.1:6379> PING
PONG

127.0.0.1:6379> SET test "Hello from TMS"
OK

127.0.0.1:6379> GET test
"Hello from TMS"
```

### Kafka
```bash
# Список топиков
docker exec -it tms-kafka kafka-topics --bootstrap-server localhost:9092 --list

# Создать топик для тестирования
docker exec -it tms-kafka kafka-topics --bootstrap-server localhost:9092 --create --topic test-topic --partitions 1 --replication-factor 1

# Producer (отправить сообщение)
docker exec -it tms-kafka kafka-console-producer --bootstrap-server localhost:9092 --topic test-topic
> Hello Kafka!
> ^C

# Consumer (получить сообщения)
docker exec -it tms-kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic test-topic --from-beginning
```

### Mailhog (тестирование email)
- **Web UI:** http://localhost:8025
- **SMTP:** localhost:1025

Все email отправленные из приложения будут здесь!

### Prometheus (метрики)
- **URL:** http://localhost:9090
- **Targets:** http://localhost:9090/targets (проверь что tms-api доступен)
- **Графики:** http://localhost:9090/graph

Примеры запросов (Prometheus Query Language):
```promql
# Количество HTTP запросов
http_server_requests_seconds_count

# Использование памяти JVM
jvm_memory_used_bytes

# CPU usage
process_cpu_usage
```

### Grafana (дашборды)
- **URL:** http://localhost:3000
- **Логин:** admin
- **Пароль:** admin

Prometheus уже настроен как источник данных!

**Импортируй готовые дашборды:**
1. Зайди в Grafana
2. Dashboards → Import
3. Введи ID готового дашборда:
    - `4701` - JVM (Micrometer)
    - `11378` - Spring Boot 2.1 System Monitor
    - `12900` - Spring Boot Statistics

---

## 🛠️ Управление сервисами

```bash
# Остановить все сервисы
docker-compose stop

# Запустить остановленные сервисы
docker-compose start

# Перезапустить сервисы
docker-compose restart

# Остановить и удалить контейнеры (данные в volumes сохранятся)
docker-compose down

# Удалить всё включая volumes (⚠️ ПОТЕРЯ ДАННЫХ!)
docker-compose down -v

# Пересобрать и запустить
docker-compose up -d --build

# Запустить только конкретные сервисы
docker-compose up -d redis kafka
```

---

## 📊 Мониторинг ресурсов

```bash
# Использование ресурсов
docker stats

# Или только TMS сервисы
docker stats tms-redis tms-kafka tms-prometheus tms-grafana
```

---

## 🔧 Настройка Spring Boot приложения

Добавь в `application.yml` или `application.properties`:

### Redis
```yaml
spring:
  redis:
    host: localhost
    port: 6379
```

### Kafka
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: tms-group
      auto-offset-reset: earliest
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

### Email (Mailhog)
```yaml
spring:
  mail:
    host: localhost
    port: 1025
    username: 
    password: 
    properties:
      mail:
        smtp:
          auth: false
          starttls:
            enable: false
```

### Actuator + Prometheus
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true
```

---

## 🧪 Тестирование интеграций

### Проверка Redis из приложения

```java
@Autowired
private RedisTemplate<String, String> redisTemplate;

public void testRedis() {
    redisTemplate.opsForValue().set("test", "Hello Redis!");
    String value = redisTemplate.opsForValue().get("test");
    System.out.println(value); // "Hello Redis!"
}
```

### Проверка Kafka из приложения

```java
@Autowired
private KafkaTemplate<String, String> kafkaTemplate;

public void testKafka() {
    kafkaTemplate.send("test-topic", "Hello Kafka from Spring Boot!");
}
```

### Проверка Email

```java
@Autowired
private JavaMailSender mailSender;

public void testEmail() {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo("test@example.com");
    message.setSubject("Test Email");
    message.setText("Hello from TMS!");
    mailSender.send(message);
    
    // Проверь Mailhog: http://localhost:8025
}
```

---

## ❓ Troubleshooting

### Redis не запускается
```bash
# Проверь что порт 6379 свободен
lsof -i :6379

# Или на Windows
netstat -ano | findstr :6379
```

### Kafka не работает
```bash
# Проверь логи
docker-compose logs kafka

# Убедись что Zookeeper запущен
docker-compose ps zookeeper
```

### Prometheus не видит приложение
1. Убедись что приложение запущено на порту 8080
2. Проверь что Actuator доступен: http://localhost:8080/actuator/prometheus
3. В Prometheus → Targets проверь статус tms-api

### Grafana не показывает данные
1. Проверь что Prometheus работает: http://localhost:9090
2. В Grafana → Configuration → Data Sources → Prometheus → Test
3. Создай простой график с запросом `up`

---

## 🎯 Что дальше?

После того как всё запустилось:

1. ✅ Добавь зависимости в `build.gradle`:
    - spring-boot-starter-data-redis
    - spring-kafka
    - spring-boot-starter-mail
    - spring-boot-starter-actuator
    - micrometer-registry-prometheus

2. ✅ Обновь `application.yml` с конфигурациями выше

3. ✅ Запусти приложение и проверь метрики в Grafana

4. ✅ Готов к **Фазе 1: Рефакторинг архитектуры**!

---

## 📝 Полезные команды

```bash
# Посмотреть все volumes
docker volume ls

# Очистить неиспользуемые volumes
docker volume prune

# Посмотреть все networks
docker network ls

# Подключиться к контейнеру
docker exec -it tms-redis sh
docker exec -it tms-kafka bash

# Экспорт/импорт данных Redis
docker exec tms-redis redis-cli SAVE
docker cp tms-redis:/data/dump.rdb ./backup-redis.rdb
```

---

Готово! Запускай: `docker-compose up -d` 🚀