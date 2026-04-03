# Payments Microservices Demo - AI Agent Context

> **Quick Start**: Run `mvn clean install -DskipTests` to verify the build. All 6 modules must compile before any changes.

## Project Overview

**Purpose**: Reference architecture for ACH/Wire payment processing using Spring Boot, Kafka event streaming, and Docker/Kubernetes.

**Stack**: Java 17, Spring Boot 3.2.5, Apache Kafka, PostgreSQL, Redis, Docker Compose, Kubernetes

---

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                     API Gateway                      │
└─────────────────────────────────────────────────────┘
                         │
        ┌────────────────┼────────────────┐
        ▼                ▼                ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│   Payment    │ │   Account    │ │ Notification │
│  Service     │ │  Service     │ │  Service     │
│   :8081      │ │   :8082      │ │   :8084      │
└──────┬───────┘ └──────┬───────┘ └──────────────┘
       │                │
       └───────┬────────┘
               ▼
      ┌─────────────────────┐
      │     Apache Kafka    │
      │ Topics: payment.*   │
      │        account.*    │
      └──────────┬──────────┘
                 │
       ┌─────────┴─────────┐
       ▼                   ▼
┌──────────────┐    ┌──────────────┐
│    Audit     │    │  Settlement  │
│  Service     │    │  Service     │
│   :8085      │    │   :8086      │
└──────────────┘    └──────────────┘
```

---

## Services

| Service | Port | Database | Package Path |
|---------|------|----------|--------------|
| payment-service | 8081 | payment_db (5431) | `com.payments.payment` |
| account-service | 8082 | account_db (5432) | `com.payments.account` |
| notification-service | 8084 | notification_db (5434) | `com.payments.notification` |
| audit-service | 8085 | audit_db (5435) | `com.payments.audit` |
| settlement-service | 8086 | settlement_db (5436) | `com.payments.settlement` |

---

## Package Structure (per service)

```
src/main/java/com/payments/{service}/
├── {ServiceName}Application.java     # Spring Boot entry point
├── controller/                       # REST endpoints
├── service/                          # Business logic
├── repository/                       # Data access
├── domain/ or model/                # Entities
├── dto/                             # Request/Response objects
├── kafka/                           # Kafka consumers/producers
├── exception/                       # Exception handlers
└── config/                         # Configuration classes
```

---

## Code Conventions

### Dependencies (pom.xml)
- Use `@Data`, `@Builder`, `@Service`, `@Repository` from Lombok
- Constructor injection via `final` fields + `@RequiredArgsConstructor` (preferred) OR explicit constructors
- `@Transactional` on service methods that modify data
- `@ControllerAdvice` for global exception handling

### Kafka Events (common module)
```java
// Events live in: com.payments.common.domain
// NOT com.payments.common.events

import com.payments.common.domain.PaymentCompletedEvent;
import com.payments.common.domain.PaymentInitiatedEvent;
import com.payments.common.kafka.KafkaTopics;
```

### Error Response (common module)
```java
// ErrorResponse is a Java RECORD, not Lombok builder
import com.payments.common.dto.ErrorResponse;

new ErrorResponse(
    "ERROR_CODE",      // errorCode
    "message",         // message  
    Map.of(),          // details (can be null)
    Instant.now(),     // timestamp
    "/api/path"        // path
)
```

### Database
- Flyway migrations in `src/main/resources/db/migration/`
- Naming: `V1__create_{table}_table.sql`
- Entities extend `com.payments.common.domain.BaseEntity` for common fields

---

## Build Commands

```bash
# Build all modules
mvn clean install -DskipTests

# Build single module
mvn clean install -pl payment-service -DskipTests

# Run tests (if any exist)
mvn test

# Resume build from failed module
mvn install -rf :payment-service -DskipTests
```

---

## Docker Compose

```bash
# Start infrastructure
docker-compose up -d

# View logs
docker-compose logs -f kafka

# Stop all
docker-compose down
```

---

## Kubernetes Deployment

```bash
kubectl apply -f k8s/
kubectl get pods -n payments
```

---

## Common Issues & Fixes

### Lombok + Java Version
- Java 17 is configured in parent pom.xml
- Lombok version: 1.18.40
- If you see annotation processor errors, verify Lombok is in pom.xml

### Flyway + PostgreSQL
- DO NOT add `flyway-database-postgresql` dependency (not needed with Flyway 9.x)
- Only `flyway-core` is required

### Kafka Event Imports
- Events are in `com.payments.common.domain`, NOT `com.payments.common.events`
- If you see "package does not exist" for events, check the import path

### Maven Build Failures
1. Run `mvn clean` first
2. Check for missing dependencies in child pom.xml
3. Verify parent pom.xml modules list includes all services

---

## Environment Variables

Each service uses environment variables (no hardcoding):

| Variable | Example |
|----------|---------|
| SERVER_PORT | 8081 |
| SPRING_KAFKA_BOOTSTRAP_SERVERS | kafka:9092 |
| SPRING_DATASOURCE_URL | jdbc:postgresql://postgres:5432/payments |
| SPRING_DATASOURCE_USERNAME | payments_user |
| SPRING_DATASOURCE_PASSWORD | (from K8s secrets) |

---

## Working with this Project

### Before Making Changes
1. Run `mvn clean install -DskipTests` to verify current state
2. Read the relevant service's controller and service files
3. Check existing patterns in other services for consistency

### After Making Changes
1. Run `mvn clean install -DskipTests` to verify compilation
2. Check for any new LSP diagnostics
3. Update relevant unit tests if adding new functionality

### Testing New Services/Features
```bash
# Start infrastructure
docker-compose up -d

# Run a service (from service directory)
mvn spring-boot:run
```

---

## Key Files

| File | Purpose |
|------|---------|
| `pom.xml` | Parent POM with dependency management |
| `common/pom.xml` | Shared library with events, DTOs, exceptions |
| `docker-compose.yml` | Local dev infrastructure |
| `k8s/configmaps.yaml` | Service configurations |
| `k8s/*-service.yaml` | K8s deployments |
| `README.md` | User-facing documentation |

---

## TODO

- [ ] Add unit tests for all services
- [ ] Implement API gateway with routing
- [ ] Add distributed tracing (Zipkin/Jaeger)
- [ ] Add Prometheus metrics
- [ ] Implement circuit breakers (Resilience4j)
- [ ] Add API documentation (OpenAPI)
