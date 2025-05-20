FROM openjdk:21
WORKDIR /app
COPY ./target/cut2short.jar /app
EXPOSE 8080
CMD ["java", "-jar", "cut2short.jar"]