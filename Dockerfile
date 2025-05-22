FROM openjdk:21
WORKDIR /app
COPY pom.xml .
COPY src ./src
#COPY ./target/cut2short.jar /app
RUN ./mvnw clean package
EXPOSE 8080
CMD ["java", "-jar", "target/cut2short.jar"]

#CMD ["java", "-jar", "cut2short.jar"]