# payments-microservices-demo

Reference architecture for ACH/Wire payment processing using Spring Boot, Kafka event streaming, and Docker/Kubernetes — modeled on real enterprise patterns.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           API Gateway                                    │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
        ┌───────────────────────────┼───────────────────────────┐
        ▼                           ▼                           ▼
┌───────────────┐          ┌───────────────┐          ┌───────────────┐
│   Payment     │          │   Account     │          │ Notification │
│   Service     │          │   Service     │          │   Service    │
│   :8081       │          │   :8082       │          │   :8083       │
└───────┬───────┘          └───────┬───────┘          └───────────────┘
        │                          │
        │  payment.*              │  account.*
        │  topics                 │  topics
        ▼                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         Apache Kafka                                     │
│  Topics: payment.initiated, payment.completed, account.updated, etc.     │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                    ┌───────────────┴───────────────┐
                    ▼                               ▼
            ┌───────────────┐              ┌───────────────┐
            │     Audit     │              │  Settlement   │
            │   Service     │              │   Service    │
            │   :8084       │              │   :8085       │
            └───────────────┘              └───────────────┘
```

## Services

| Service | Port | Description |
|---------|------|-------------|
| payment-service | 8081 | Core ACH/Wire payment processing |
| account-service | 8082 | Account balances and fund holds |
| notification-service | 8083 | Email/SMS/Webhook notifications |
| audit-service | 8084 | Immutable audit trail |
| settlement-service | 8085 | End-of-day settlement processing |

## Tech Stack

- **Java 21** with Spring Boot 3.2
- **Apache Kafka** for event streaming
- **PostgreSQL** for data persistence (per-service databases)
- **Redis** for caching
- **Docker & Docker Compose** for local development
- **Kubernetes** manifests for production deployment

## Quick Start

### Prerequisites
- Java 21
- Docker & Docker Compose
- Maven 3.9+

### 1. Build the Project
```bash
mvn clean install
```

### 2. Start Infrastructure
```bash
docker-compose up -d
```

### 3. Run Services
```bash
# Terminal 1: Payment Service
cd payment-service && mvn spring-boot:run

# Terminal 2: Account Service  
cd account-service && mvn spring-boot:run

# Terminal 3: Notification Service
cd notification-service && mvn spring-boot:run

# Terminal 4: Audit Service
cd audit-service && mvn spring-boot:run

# Terminal 5: Settlement Service
cd settlement-service && mvn spring-boot:run
```

### 4. Access Services
- Payment Service: http://localhost:8081
- Account Service: http://localhost:8082
- Notification Service: http://localhost:8083
- Audit Service: http://localhost:8084
- Settlement Service: http://localhost:8085
- Kafka UI: http://localhost:8090

## API Examples

### Create Payment
```bash
curl -X POST http://localhost:8081/api/v1/payments \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountId": "acc-123",
    "toAccountId": "acc-456",
    "amount": 1000.00,
    "currency": "USD",
    "paymentType": "ACH",
    "routingNumber": "021000021",
    "accountNumber": "123456789",
    "description": "Payment to vendor"
  }'
```

### Create Account
```bash
curl -X POST http://localhost:8082/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "accountHolderName": "John Doe",
    "initialBalance": 10000.00
  }'
```

## Event Flow

```
1. POST /payments → PaymentService → emit payment.initiated
2. AccountService consumes → validates funds → emit account.updated
3. PaymentService consumes → updates status → emit payment.completed
4. NotificationService consumes → sends email
5. AuditService consumes → logs all events
```

## Kafka Topics

| Topic | Description |
|-------|-------------|
| payment.initiated | New payment created |
| payment.validated | Payment passed validation |
| payment.completed | Payment succeeded |
| payment.failed | Payment failed |
| account.updated | Account balance changed |
| account.hold.created | Fund hold created |
| account.hold.released | Fund hold released |

## Kubernetes Deployment

```bash
kubectl apply -f k8s/
```

## Project Structure

```
payments-microservices-demo/
├── common/                    # Shared models, events, utilities
├── payment-service/           # Core payment processing
├── account-service/           # Account management
├── notification-service/      # Notifications
├── audit-service/             # Audit logging
├── settlement-service/        # Settlement processing
├── docker-compose.yml         # Local development
└── k8s/                      # Kubernetes manifests
```

## TODO

- [ ] Add unit tests for all services
- [ ] Implement API gateway with routing
- [ ] Add distributed tracing (Zipkin/Jaeger)
- [ ] Add Prometheus metrics
- [ ] Implement circuit breakers (Resilience4j)
- [ ] Add API documentation (OpenAPI)
