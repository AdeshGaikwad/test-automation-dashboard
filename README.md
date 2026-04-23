# 🧪 Test Automation Dashboard

A centralized, real-time dashboard for monitoring and managing test automation results. Built with **Spring Boot**, backed by **PostgreSQL** and **Redis**, and fully containerized with **Docker**.

---

## 📌 Overview

The Test Automation Dashboard provides a unified platform to track, store, and visualize test execution results across multiple projects and environments. It exposes REST APIs that can be integrated with any test automation framework (Selenium, TestNG, JUnit, Playwright, etc.) to push test results in real time.

---

## 🚀 Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot |
| Database | PostgreSQL 15 |
| Cache | Redis 7 |
| Build Tool | Maven |
| Containerization | Docker, Docker Compose |

---

## 📁 Project Structure

```
test-automation-dashboard/
├── src/
│   ├── main/
│   │   ├── java/         # Application source code
│   │   └── resources/    # Configuration files
│   └── test/             # Unit & integration tests
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## ⚙️ Prerequisites

Make sure you have the following installed:

- [Java 21+](https://adoptium.net/)
- [Maven 3.9+](https://maven.apache.org/)
- [Docker](https://www.docker.com/) & [Docker Compose](https://docs.docker.com/compose/)

---

## 🐳 Running with Docker (Recommended)

The easiest way to run the full stack (app + PostgreSQL + Redis):

```bash
docker-compose up --build
```

This will start:
- **App** on `http://localhost:8081`
- **PostgreSQL** on port `5433`
- **Redis** on port `6380`

To stop:

```bash
docker-compose down
```

---

## 🛠️ Running Locally (Without Docker)

### 1. Start PostgreSQL and Redis

Make sure PostgreSQL and Redis are running locally, or use Docker just for the dependencies:

```bash
docker-compose up postgres redis
```

### 2. Configure Application Properties

Update `src/main/resources/application.properties` (or `application.yml`) with your local DB credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/test_dashboard_db
spring.datasource.username=postgres
spring.datasource.password=postgres

spring.redis.host=localhost
spring.redis.port=6380
```

### 3. Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

The app will be available at `http://localhost:8081`

---

## 🔌 API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/results` | Submit test results |
| `GET` | `/api/results` | Fetch all test results |
| `GET` | `/api/results/{id}` | Fetch result by ID |
| `GET` | `/api/dashboard` | Get dashboard summary |

> 📝 Full API documentation available at `http://localhost:8081/swagger-ui.html` (if Swagger is enabled)

---

## 🧪 Running Tests

```bash
mvn test
```

---

## 🌍 Environment Variables

The following environment variables can be configured (used in Docker):

| Variable | Default | Description |
|---|---|---|
| `DB_USERNAME` | `postgres` | PostgreSQL username |
| `DB_PASSWORD` | `postgres` | PostgreSQL password |
| `REDIS_HOST` | `redis` | Redis hostname |
| `REDIS_PORT` | `6379` | Redis port |

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -m 'Add your feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a Pull Request
