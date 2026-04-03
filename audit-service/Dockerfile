FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

COPY pom.xml .
COPY common/pom.xml common/
COPY audit-service/pom.xml audit-service/

RUN apk add --no-cache maven

RUN mvn dependency:go-offline -B

COPY common/src common/src
COPY audit-service/src audit-service/src

RUN mvn package -DskipTests -B -pl audit-service -am

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/audit-service/target/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]
