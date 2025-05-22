package com.url.shortner.config

import com.url.shortner.utils.AuditorAwareImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
open class JpaAuditingConfiguration {

    @Bean
    open fun auditorProvider(): AuditorAware<String> {
        return AuditorAwareImpl()
    }
}