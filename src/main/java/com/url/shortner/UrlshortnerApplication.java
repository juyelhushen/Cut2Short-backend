package com.url.shortner;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@SpringBootApplication
@OpenAPIDefinition(info =
@Info(title = "Url Shortner's API", version = "1.0", description = "Documentation Authentication API v1.0"))
@EnableScheduling
//@RestController
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UrlshortnerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrlshortnerApplication.class, args);
    }
//
//    private final RestTemplate restTemplate;
//
//
//    @GetMapping("/test")
//    public ResponseEntity<String> test() {
//        var url = "https://dummy.restapiexample.com/api/v1/employees";
////        try {
////            HttpClient client = HttpClient.newHttpClient();
////
////            HttpRequest request = HttpRequest.newBuilder()
////                    .uri(URI.create("https://dummy.restapiexample.com/api/v1/employees"))
////                    .header("Accept", "application/json")
////                    .GET()
////                    .build();
////
////            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
////
////            // Log and return the JSON response
////            log.info("Status Code: {}", response.statusCode());
////            log.info("Response Body: {}", response.body());
////
////            return ResponseEntity.ok(response.body());
////
////        } catch (Exception e) {
////            e.printStackTrace();
////            return ResponseEntity.status(500).body("Error occurred while calling the external API.");
////        }
//        var response = restTemplate.getForEntity(url, String.class);
//        return ResponseEntity.ok(response.getBody());
//
//    }

}
