package com.url.shortner.security.user;

import com.url.shortner.entity.Role;
import com.url.shortner.entity.User;
import com.url.shortner.repository.UserRepository;
import com.url.shortner.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository repository;
    private final JwtUtils jwtUtils;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("loadUser : {}", oAuth2User);

        String email = oAuth2User.getAttribute("email");
        if (email == null) {  //fallback
            log.warn("Email not provided by GitHub, attempting to use login as email.");
            email = oAuth2User.getAttribute("login") + "@github.com";
        }

        String name = oAuth2User.getAttribute("name");
        String finalEmail = email;
        User user = repository.findByEmail(email).orElseGet(() -> {
            String[] names = name != null ? name.split(" ") : new String[]{"Unknown"};
            User newUser = User.builder()
                    .firstName(names[0])
                    .lastName(names.length > 1 ? names[1] : null)
                    .email(finalEmail)
                    .role(Role.USER)
                    .build();
            return repository.save(newUser);
        });

        var token = jwtUtils.generateFromOAuth2User(email, Role.USER);

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("token", token);
        attributes.put("userid", user.getId());

        String principalAttribute = oAuth2User.getAttribute("email") != null ? "email" : "login";


        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                attributes,
                principalAttribute
        );
    }
}
