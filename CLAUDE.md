# CLAUDE.md

## Project Overview

Payments Microservices Demo - Spring Boot microservices for ACH/Wire payment processing.

## Quick Start

```bash
mvn clean install -DskipTests  # Build all 6 modules
docker-compose up -d           # Start infrastructure
```

## Architecture

- 5 microservices: payment, account, notification, audit, settlement
- Kafka for event streaming
- PostgreSQL per service
- Kubernetes manifests in `k8s/`

## Critical Conventions

### Package Paths
- Events: `com.payments.common.domain` (NOT `com.payments.common.events`)
- Common DTOs: `com.payments.common.dto`
- Services: `com.payments.{service}`

### Important Patterns
- `ErrorResponse` is a Java **record**, not Lombok builder
- Use constructor injection (`@RequiredArgsConstructor` or explicit constructors)
- All config via environment variables, no hardcoding
- `@Transactional` on service methods that modify data

### Common Errors
- **"package com.payments.common.events does not exist"** → Change to `com.payments.common.domain`
- **"builder() is undefined for ErrorResponse"** → Use `new ErrorResponse(...)` constructor
- **"flyway-database-postgresql:jar is missing"** → Remove dependency (not needed with Flyway 9.x)

## Key Files

- `pom.xml` - Parent Maven config
- `common/` - Shared events, DTOs, exceptions
- `k8s/` - Kubernetes manifests
- `docker-compose.yml` - Local dev infrastructure

## Build Verification

```bash
mvn clean install -DskipTests
```

Must show `BUILD SUCCESS` with all 6 modules before submitting changes.
