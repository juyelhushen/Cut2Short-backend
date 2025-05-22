# Stage 1: Build the application
FROM openjdk:21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package

# Stage 2: Create the runtime image
FROM openjdk:21-slim
WORKDIR /app
COPY --from=builder /app/target/cut2short.jar /app/cut2short.jar
EXPOSE 8080
CMD ["java", "-Xms256m", "-Xmx512m", "-jar", "cut2short.jar"]