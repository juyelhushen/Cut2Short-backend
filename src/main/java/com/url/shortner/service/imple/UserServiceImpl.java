package com.url.shortner.service.imple;

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
            Optional<User> user = userRepository.findByEmail(request.email());
            if (user.isPresent()) throw new IllegalArgumentException("email already in use");
            User savedUser = userRepository.save(saveUser(request));

            Authentication auth = new UsernamePasswordAuthenticationToken(request.email(), request.password());
            SecurityContextHolder.getContext().setAuthentication(auth);

            String token = jwtUtils.generate(savedUser.getId(), savedUser.getEmail(), Role.USER);
            return new AuthResponse(savedUser.getFullName(), savedUser.getEmail(), token);

        }
        return null;
    }

    @Override
    public AuthResponse login(UserRequest request) {
        log.info("checking username {}", request.email());
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

            log.info("is authenticated value : {}", auth.isAuthenticated());
            if (auth.isAuthenticated()) {
                User user = findByUsername(request.email());
                SecurityContextHolder.getContext().setAuthentication(auth);
                String token = jwtUtils.generate(user.getId(), user.getEmail(), Role.USER);
                return new AuthResponse(user.getFullName(), user.getEmail(), token);
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

    private User findByUsername(String username) {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }


    private boolean isValid(UserRequest request) {
        return request.firstName() != null && request.email() != null && request.password() != null;
    }
}
