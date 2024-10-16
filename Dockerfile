FROM maven:3.8.1-openjdk-17-slim AS builder
LABEL authors="TienCao"

WORKDIR /app
COPY pom.xml .
RUN mvn clean install
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=builder /app/target/cinema-at-manhattan-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]


