# Cinema Java Spring

REST API для управления кинотеатром, построенное на Spring Boot с использованием PostgreSQL в качестве базы данных.

## Технологии

- **Java 21** - основной язык программирования
- **Spring Boot** - веб-фреймворк
- **PostgreSQL** - реляционная база данных
- **Liquibase** - для миграций базы данных
- **OpenAPI/Swagger** - для документации API
- **Testcontainers** - для интеграционного тестирования
- **Maven** - управление зависимостями
- **Docker** - контейнеризация

## Установка и запуск

### Локальный запуск

1. **Клонируйте репозиторий:**
```bash
git clone <repository-url>
cd <repository-url>
```

2. **Запустите базу данных через Docker:**
```bash
docker-compose up -d db-cinema-java
```

3. **Установите переменные окружения:**
```bash
export POSTGRES_USER=postgres
export POSTGRES_PASSWORD=postgres
export POSTGRES_DB=cinema_db
export POSTGRES_PORT=5432
```

4. **Запустите приложение:**
```bash
./mvnw spring-boot:run
```

### Запуск через Docker Compose

```bash
docker-compose up -d
```

Приложение будет доступно по адресу: `http://localhost:8080`

## API Документация

После запуска приложения документация API доступна по адресам:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## Тестирование

Проект включает:
- **Unit тесты** для сервисов
- **Integration тесты** для контроллеров с использованием Testcontainers

Запуск тестов:
```bash
./mvnw test
```

## База данных

Используется PostgreSQL с миграциями через Liquibase. Схема базы данных находится в `src/main/resources/db/changelog/`.

## TODO

- [ ] добавить проверку в ci покрытие тестами

- [ ] movie
  - [ ] service tests refactor 
  - [ ] controller tests refacrtor
  - [ ] check validation on fiter
  - [ ] what filters add

- [ ] watched movie
  - [ ] create service tests
  - [ ] create controller tests  

- [ ] version in Base Entity; OptimisticLock

- [ ] **Аутентификация и авторизация**
  - [ ] Добавить Spring Security
  - [ ] Реализовать JWT токены/ или использовать SSO
  - [ ] Создать роли пользователей (USER, ADMIN)

- [ ] **Улучшение API**
  - [ ] Добавить пагинацию для списков
  - [ ] Реализовать поиск и фильтрацию фильмов
  - [ ] Добавить сортировку по различным критериям

- [ ] **Производительность и масштабируемость**
  - [ ] Добавить кэширование (Redis)
  - [ ] Оптимизация запросов к базе данных
  - [ ] Добавить метрики и мониторинг
  - [ ] Prometheus

- [ ] WARN 237936 --- [cinema-java-spring] [nio-8080-exec-6] org.hibernate.orm.query: HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory
