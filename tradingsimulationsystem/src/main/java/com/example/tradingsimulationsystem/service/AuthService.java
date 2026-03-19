package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.domain.User;
import com.example.tradingsimulationsystem.dto.LoginRequest;
import com.example.tradingsimulationsystem.dto.RegisterRequest;
import com.example.tradingsimulationsystem.dto.AuthResponse;
import com.example.tradingsimulationsystem.repository.UserRepository;
import com.example.tradingsimulationsystem.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("com.example.tradingsimulationsystem.audit");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Register a new user: encode password, save in DB, and return both access & refresh tokens.
     */
    public AuthResponse register(RegisterRequest request) {
        logger.info("Registration attempt for username={}", request.getUsername());

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            logger.warn("Registration failed: Username {} already exists", request.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setBalance(50000.0);       // default starting balance
        user.setMarginAllowed(5.0);    // default margin
        user.setMarginUsed(0.0);       // safe default
        user.setRole("USER");          // default role

        userRepository.save(user);
        logger.info("User {} registered successfully with id={}", user.getUsername(), user.getId());
        auditLogger.info("New user registered: username={}, id={}", user.getUsername(), user.getId());

        String accessToken = jwtUtil.generateAccessToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        return new AuthResponse(accessToken, refreshToken, Math.toIntExact(user.getId()), user.getUsername());
    }

    /**
     * Login: authenticate and generate both access & refresh tokens.
     */
    public AuthResponse login(LoginRequest loginRequest) {
        logger.info("Login attempt for username={}", loginRequest.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        String accessToken = jwtUtil.generateAccessToken(loginRequest.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(loginRequest.getUsername());
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();

        logger.info("Login successful for username={}, userId={}", user.getUsername(), user.getId());
        auditLogger.info("User {} logged in successfully", user.getUsername());

        return new AuthResponse(accessToken, refreshToken, Math.toIntExact(user.getId()), user.getUsername());
    }

    /**
     * Refresh access token using a valid refresh token.
     */
    public AuthResponse refreshAccessToken(String refreshToken) {
        logger.info("Access token refresh attempt");

        if (jwtUtil.validateRefreshToken(refreshToken)) {
            String username = jwtUtil.extractUsernameFromRefresh(refreshToken);
            String newAccessToken = jwtUtil.generateAccessToken(username);
            User user = userRepository.findByUsername(username).orElseThrow();

            logger.info("Access token refreshed successfully for username={}, userId={}", username, user.getId());
            auditLogger.info("User {} refreshed access token", username);

            // return same refresh token (still valid) with new access token
            return new AuthResponse(newAccessToken, refreshToken, Math.toIntExact(user.getId()), user.getUsername());
        } else {
            logger.warn("Invalid or expired refresh token used");
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }
    }
}
