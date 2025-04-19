package com.url.shortner;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.servlet.http.Part;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@OpenAPIDefinition(info =
@Info(title = "Url Shortner's API", version = "1.0", description = "Documentation Authentication API v1.0"))
public class UrlshortnerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrlshortnerApplication.class, args);
    }

}
