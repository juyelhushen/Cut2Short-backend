package com.url.shortner.config

import org.modelmapper.ModelMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
open class AppConfig {

    @Bean
    open fun modelMapper(): ModelMapper {
        return ModelMapper();
    }

    @Bean
    open fun restTemplate(): RestTemplate {
        return RestTemplate();
    }
}