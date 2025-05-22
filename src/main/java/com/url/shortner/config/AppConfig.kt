package com.url.shortner.config

import org.modelmapper.ModelMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AppConfig {

    @Bean
    open fun modelMapper(): ModelMapper {
        return ModelMapper();
    }
}