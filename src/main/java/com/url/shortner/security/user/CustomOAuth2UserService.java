package com.url.shortner.security.user;

import com.url.shortner.entity.Role;
import com.url.shortner.entity.User;
import com.url.shortner.repository.UserRepository;
import com.url.shortner.security.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("loadUser: {}", oAuth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String email = oAuth2User.getAttribute("email");
        String oauth2Id;

        if ("github".equals(provider)) {
            if (email == null) {
                email = oAuth2User.getAttribute("login") + "@github.com";
            }
            oauth2Id = "github_" + oAuth2User.getAttribute("id");
        } else { // google
            oauth2Id = "google_" + oAuth2User.getAttribute("sub");
        }

        String imageUrl = oAuth2User.getAttribute("picture");
        String name = oAuth2User.getAttribute("name");
        String finalEmail = email;

        User user = userRepository.findByOauth2Id(oauth2Id)
                .orElseGet(() -> userRepository.findByEmail(finalEmail)
                        .orElseGet(() -> {
                            String[] names = name != null ? name.split(" ") : new String[]{"Unknown"};
                            User newUser = User.builder()
                                    .firstName(names[0])
                                    .lastName(names.length > 1 ? names[1] : null)
                                    .email(finalEmail)
                                    .role(Role.USER)
                                    .imageUrl(imageUrl)
                                    .oauth2Id(oauth2Id)
                                    .build();
                            User savedUser = userRepository.save(newUser);
                            log.info("Created new user: id={}, email={}", savedUser.getId(), finalEmail);
                            return savedUser;
                        }));

        var token = jwtUtils.generateFromOAuth2User(user.getEmail(), user.getRole());

        // 🔑 Store JWT in HttpOnly cookie
        HttpServletResponse response = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
        if (response != null) {
            ResponseCookie cookie = ResponseCookie.from("AUTH-TOKEN", token)
                    .httpOnly(true)
                    .secure(true) // ❌ keep false for local dev, ✅ set true in production
                    .sameSite("None") // or "Strict" for extra security
                    .path("/")
                    .maxAge(Duration.ofDays(7))
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("token", token);
        attributes.put("userId", user.getId());
        attributes.put("profile", imageUrl);

        String principalAttribute = "email";

        OAuth2User customUser = new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                attributes,
                principalAttribute
        );

        SecurityContextHolder.getContext().setAuthentication(
                new OAuth2AuthenticationToken(
                        customUser,
                        customUser.getAuthorities(),
                        provider
                )
        );

        log.info("SecurityContextHolder set with user: id={}, email={}", user.getId(), finalEmail);
        return customUser;
    }
}