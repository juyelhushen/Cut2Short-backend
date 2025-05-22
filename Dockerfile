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


# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21 as builder

# Set working directory
WORKDIR /app

# Copy only the files needed for building to take advantage of Docker caching
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre-jammy

# Set working directory
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar cut2short.jar

# Expose the port your app runs on (change 8080 to your actual port if different)
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "cut2short.jar"]