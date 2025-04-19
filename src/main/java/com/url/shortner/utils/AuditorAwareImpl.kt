package com.url.shortner.utils

import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import java.util.Optional

class AuditorAwareImpl : AuditorAware<String> {
    override fun getCurrentAuditor(): Optional<String> {
        val authentication: Authentication? = SecurityContextHolder.getContext().authentication

        return if (authentication == null || !authentication.isAuthenticated) Optional.empty()
        else Optional.ofNullable(authentication.name)
    }
}