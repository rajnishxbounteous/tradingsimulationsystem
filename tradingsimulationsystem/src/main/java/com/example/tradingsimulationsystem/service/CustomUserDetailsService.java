package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.domain.User;
import com.example.tradingsimulationsystem.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("com.example.tradingsimulationsystem.audit");

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempting to load user by username={}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found during authentication: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });

        logger.info("User {} found with id={}", user.getUsername(), user.getId());
        auditLogger.info("Authentication lookup performed for username={}", user.getUsername());

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("USER") // simple role for now
                .build();
    }
}
