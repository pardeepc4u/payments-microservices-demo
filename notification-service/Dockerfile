FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

COPY pom.xml .
COPY common/pom.xml common/
COPY notification-service/pom.xml notification-service/

RUN apk add --no-cache maven

RUN mvn dependency:go-offline -B

COPY common/src common/src
COPY notification-service/src notification-service/src

RUN mvn package -DskipTests -B -pl notification-service -am

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/notification-service/target/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
