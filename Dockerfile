## Stage 1: Build the application
#FROM openjdk:21 AS builder
#WORKDIR /app
#COPY pom.xml .
#COPY src ./src
#RUN mvn package
#
## Stage 2: Create the runtime image
#FROM openjdk:21-slim
#WORKDIR /app
#COPY --from=builder /app/target/cut2short.jar /app/cut2short.jar
#EXPOSE 8080
#CMD ["java", "-Xms256m", "-Xmx512m", "-jar", "cut2short.jar"]

# Use a Java 21 base image
FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN ./mvnw clean package
EXPOSE 8080
CMD ["java", "-jar", "target/cut2short.jar"]