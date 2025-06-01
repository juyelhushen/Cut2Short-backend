package com.url.shortner.security.user;

import com.url.shortner.entity.User;
import com.url.shortner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

//@Slf4j
//@Service
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//public class UserDetailServiceImpl implements UserDetailsService {
//
//    private final UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        log.info("Inside loadUserByUsername method for user: {}", username);
//
//        User user = userRepository.findByEmail(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
//
//        log.info("User found: {} | Role: {}", user.getEmail(), user.getRole());
//
//        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
//        List<GrantedAuthority> authorities = Collections.singletonList(authority);
//
//        String password = user.getPassword() != null ? user.getPassword() : "{noop}oauth_user";
//
//        return new org.springframework.security.core.userdetails.User(
//                user.getEmail(), password, authorities);
//    }
//}

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Inside loadUserByUsername method for user: {}", username);

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", username);
                    return new UsernameNotFoundException("User not found with email: " + username);
                });

        log.info("User found: {} | Role: {}", user.getEmail(), user.getRole());

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        List<GrantedAuthority> authorities = Collections.singletonList(authority);

        String password = user.getPassword() != null ? user.getPassword() : "{noop}oauth_user";

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), password, authorities);
    }
}

