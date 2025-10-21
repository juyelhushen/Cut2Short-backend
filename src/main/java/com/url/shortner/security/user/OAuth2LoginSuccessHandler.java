package com.url.shortner.security.user;

import com.url.shortner.entity.Role;
import com.url.shortner.security.JwtUtils;
import com.url.shortner.security.cookie.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${callback.url}")
    private String callbackUrl;

    @Value("${cors.url}")
    private String corsUrl;

    private final CookieService cookieService;
    private final JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
        String email = (String) oauthUser.getAttributes().get("email");
        String role = oauthUser.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        String token = jwtUtils.generateFromOAuth2User(email, Role.valueOf(role));

        ResponseCookie cookie = cookieService.createCookie(token);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        response.sendRedirect(callbackUrl);
    }
}
