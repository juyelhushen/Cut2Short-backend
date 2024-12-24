package com.url.shortner;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableJpaAuditing
@SpringBootApplication
@OpenAPIDefinition(info =
@Info(title = "Url Shortner's API", version = "1.0", description = "Documentation Authentication API v1.0"))
public class UrlshortnerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrlshortnerApplication.class, args);
    }


    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    public String hello() {
        return "Hello World";
    }

}
