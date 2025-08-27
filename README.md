# Cut2Short

[![Java](https://img.shields.io/badge/Java-21-blue)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-blue)](https://reactjs.org/)
[![Kafka](https://img.shields.io/badge/Kafka-3.5-orange)](https://kafka.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

Cut2Short is a modern URL shortening application (like Bitly) built with the latest technologies. Users can shorten URLs, manage links, generate QR codes, and track usage. The system supports OAuth2 login, JWT authentication (via HTTP-only cookies), real-time notifications using WebSocket and Kafka, and handles high concurrency with multithreading.

---

## Features

- Shorten long URLs
- List all URLs for a logged-in user
- Update and delete URLs
- Generate QR codes for URLs
- User registration, login, and OAuth2 login
- JWT-based authentication (HTTP-only cookies)
- Real-time notifications using **Kafka + WebSocket**
- Concurrent processing with **multithreading** for improved performance
- Responsive UI built with React 19
- API documentation via Swagger

---

## Tech Stack

**Backend:**  
- Java 21 (latest stable)  
- Spring Boot 3.5  
- Spring Security (JWT & OAuth2)  
- Spring Data JPA (MySQL/PostgreSQL)  
- Kafka 3.5 for messaging & notifications  
- WebSockets for real-time notifications  
- Multithreading / ExecutorService for high concurrency  
- Swagger / Springdoc OpenAPI  

**Frontend:**  
- React 19 (latest stable)  
- Axios for API calls  
- Tailwind CSS  

**Others:**  
- Docker for containerization  
- Render for deployment  

---

## Deployment

**Backend:** `https://cut2short-backend.onrender.com`  
**Frontend:** `https://cut2short-front.onrender.com`  

---

## API Documentation

Swagger UI is available at:  
