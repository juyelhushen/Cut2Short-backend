package com.url.shortner.service.imple;

import com.url.shortner.entity.Role;
import com.url.shortner.entity.User;
import com.url.shortner.payload.UserRequest;
import com.url.shortner.repository.UserRepository;
import com.url.shortner.security.AuthFilter;
import com.url.shortner.security.JwtUtils;
import com.url.shortner.service.UserService;
import com.url.shortner.wrapper.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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


    @Override
    public AuthResponse register(UserRequest request) {
        if (isValid(request)) {
            Optional<User> user = userRepository.findByUsername(request.username());
            if (user.isPresent()) throw new IllegalArgumentException("username already in use");
            User savedUser = userRepository.save(saveUser(request));

            Authentication auth = new UsernamePasswordAuthenticationToken(request.username(), request.password());
            SecurityContextHolder.getContext().setAuthentication(auth);

            String token = jwtUtils.generate(savedUser.getId(), savedUser.getUsername(), Role.USER);
            return new AuthResponse(savedUser.getFullName(), savedUser.getUsername(), token);

        }
        return null;
    }

    @Override
    public AuthResponse login(UserRequest request) {
        log.info("checking username {}", request.username());
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            log.info("is authenticated value : {}", auth.isAuthenticated());
            if (auth.isAuthenticated()) {
                User user = findByUsername(request.username());
                SecurityContextHolder.getContext().setAuthentication(auth);
                String token = jwtUtils.generate(user.getId(), user.getUsername(), Role.USER);
                return new AuthResponse(user.getFullName(), user.getUsername(), token);
            }

            return null;

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Bad credentials! Invalid username or password!");
        }
    }

    private User saveUser(UserRequest userRequest) {
        return User.builder()
                .firstName(userRequest.firstName())
                .lastName(userRequest.lastName())
                .username(userRequest.username())
                .password(passwordEncoder.encode(userRequest.password()))
                .role(Role.USER)
                .build();
    }

    private User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }


    private boolean isValid(UserRequest request) {
        return request.firstName() != null && request.username() != null && request.password() != null;
    }
}
