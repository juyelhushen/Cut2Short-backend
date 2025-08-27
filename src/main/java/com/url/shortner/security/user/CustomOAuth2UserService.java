package com.url.shortner.security.user;

import com.url.shortner.entity.Role;
import com.url.shortner.entity.User;
import com.url.shortner.repository.UserRepository;
import com.url.shortner.security.JwtUtils;
import com.url.shortner.security.cookie.CookieService;
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

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("loadUser: {}", oAuth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String email = oAuth2User.getAttribute("email");
        String oauth2Id;

        if ("github".equals(provider)) {
            if (email == null) email = oAuth2User.getAttribute("login") + "@github.com";
            oauth2Id = "github_" + oAuth2User.getAttribute("id");
        } else {
            oauth2Id = "google_" + oAuth2User.getAttribute("sub");
        }

        String name = oAuth2User.getAttribute("name");
        String[] names = name != null ? name.split(" ") : new String[]{"Unknown"};

        String finalEmail = email;
        User user = userRepository.findByOauth2Id(oauth2Id)
                .orElseGet(() -> userRepository.findByEmail(finalEmail)
                        .orElseGet(() -> {
                            User newUser = User.builder()
                                    .firstName(names[0])
                                    .lastName(names.length > 1 ? names[1] : null)
                                    .email(finalEmail)
                                    .role(Role.USER)
                                    .oauth2Id(oauth2Id)
                                    .build();
                            User saved = userRepository.save(newUser);
                            log.info("Created new user: id={}, email={}", saved.getId(), finalEmail);
                            return saved;
                        }));

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("userId", user.getId());

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                attributes,
                "email"
        );
    }
}
