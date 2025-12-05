# Step 1: Build the JAR
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Step 2: Run built JAR
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/target/moneyManager-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]
