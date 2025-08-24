package com.url.shortner.service.imple;

import com.url.shortner.entity.Role;
import com.url.shortner.entity.User;
import com.url.shortner.mapper.UserMapper;
import com.url.shortner.payload.UserRequest;
import com.url.shortner.payload.UserUpdateRequest;
import com.url.shortner.repository.UserRepository;
import com.url.shortner.security.AuthFilter;
import com.url.shortner.security.JwtUtils;
import com.url.shortner.security.cookie.CookieService;
import com.url.shortner.service.UserService;
import com.url.shortner.wrapper.AuthResponse;
import com.url.shortner.wrapper.ProfileResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final AuthFilter authFilter;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final CookieService cookieService;
    private final UserMapper userMapper;

    @Override
    public AuthResponse register(UserRequest request) {
        if (isValid(request)) {
            Optional<User> user = userRepository.findByEmail(request.email());
            if (user.isPresent()) throw new IllegalArgumentException("email already in use");
            User savedUser = userRepository.save(saveUser(request));

            Authentication auth = new UsernamePasswordAuthenticationToken(request.email(), request.password());
            SecurityContextHolder.getContext().setAuthentication(auth);

            String token = jwtUtils.generate(savedUser.getId(), savedUser.getEmail(), Role.USER);

            HttpServletResponse httpResponse = ((ServletRequestAttributes) Objects.requireNonNull(
                    RequestContextHolder.getRequestAttributes()))
                    .getResponse();

            if (token != null && httpResponse != null) {
                ResponseCookie cookie = cookieService.createCookie(token);
                httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            }

            return new AuthResponse(savedUser.getId(), savedUser.getFullName(), savedUser.getEmail(), savedUser.getImageUrl(), savedUser.getRole());

        }
        return null;
    }

    @Override
    public AuthResponse login(UserRequest request) {
        log.info("checking username {}", request.email());
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

            log.info("is authenticated value : {}", auth.isAuthenticated());
            if (auth.isAuthenticated()) {
                User user = findByUsername(request.email());
                SecurityContextHolder.getContext().setAuthentication(auth);
                String token = jwtUtils.generate(user.getId(), user.getEmail(), Role.USER);

                HttpServletResponse httpResponse = ((ServletRequestAttributes) Objects.requireNonNull(
                        RequestContextHolder.getRequestAttributes()))
                        .getResponse();

                if (token != null && httpResponse != null) {
                    ResponseCookie cookie = cookieService.createCookie(token);
                    httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                }
                return new AuthResponse(user.getId(), user.getFullName(), user.getEmail(), user.getImageUrl(), user.getRole());
            }
            return null;

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Bad credentials! Invalid email or password!");
        }
    }

    private User saveUser(UserRequest userRequest) {
        return User.builder()
                .firstName(userRequest.firstName())
                .lastName(userRequest.lastName())
                .email(userRequest.email())
                .password(passwordEncoder.encode(userRequest.password()))
                .role(Role.USER)
                .build();
    }


    @Override
    public ProfileResponse fetchUserProfileInfo(String token) {
        var username = jwtUtils.extractUsername(token);
        return userRepository
                .findByEmail(username)
                .map(ProfileResponse::new)
                .orElse(null);
    }

    @Override
    public boolean updateProfile(UserUpdateRequest request, String token) {
        var username = jwtUtils.extractUsername(token);
        return userRepository.findByEmail(username)
                .map(user -> {
                    userMapper.updateUserFromRequest(request, user);
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    private User findByUsername(String username) {
        return userRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }


    private boolean isValid(UserRequest request) {
        return request.firstName() != null && request.email() != null && request.password() != null;
    }
}
