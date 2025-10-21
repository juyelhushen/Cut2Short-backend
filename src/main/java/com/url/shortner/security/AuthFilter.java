package com.url.shortner.security;

import com.url.shortner.security.user.UserDetailServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailServiceImpl userDetailService;

    private static final String[] WHITELIST = {
//            "/api/v1/url/shorten",
//            "/api/v1/auth/login",
//            "/api/v1/auth/register",
            "/oauth2/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs.yaml",
            "/api/user/me"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        boolean shouldSkip = Arrays.stream(WHITELIST)
                .map(pattern -> pattern.endsWith("/**") ? pattern.substring(0, pattern.length() - 3) : pattern)
                .anyMatch(path::startsWith);
        log.info("Checking path: {} | Should skip filter: {}", path, shouldSkip);
        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("Processing AuthFilter for: {}", request.getRequestURI());
        String header = request.getHeader("Authorization");
        String username = null;
        String token = null;

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            log.info("Received token");
            try {
                username = jwtUtils.extractUsername(token);
                log.info("Extracted username: {}", username);
            } catch (Exception e) {
                log.error("Error extracting username: {}", e.getMessage(), e);
            }
        } else {
            log.warn("No or invalid Authorization header: {}", header);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailService.loadUserByUsername(username);
                log.info("UserDetails: username={}, authorities={}",
                        userDetails.getUsername(), userDetails.getAuthorities());
                if (jwtUtils.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("Authentication set for: {}", username);
                } else {
                    log.error("Invalid token for: {}", username);
                }
            } catch (Exception e) {
                log.error("Error loading UserDetails: {}", e.getMessage(), e);
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}