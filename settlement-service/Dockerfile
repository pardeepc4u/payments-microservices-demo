FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

COPY pom.xml .
COPY common/pom.xml common/
COPY settlement-service/pom.xml settlement-service/

RUN apk add --no-cache maven

RUN mvn dependency:go-offline -B

COPY common/src common/src
COPY settlement-service/src settlement-service/src

RUN mvn package -DskipTests -B -pl settlement-service -am

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/settlement-service/target/*.jar app.jar

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "app.jar"]
