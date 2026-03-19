package com.example.tradingsimulationsystem.controller;

import com.example.tradingsimulationsystem.dto.LoginRequest;
import com.example.tradingsimulationsystem.dto.RegisterRequest;
import com.example.tradingsimulationsystem.dto.AuthResponse;
import com.example.tradingsimulationsystem.dto.RefreshRequest;
import com.example.tradingsimulationsystem.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("com.trading.simulation.audit");

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        logger.info("Registration attempt for username={}", registerRequest.getUsername());
        try {
            AuthResponse tokens = authService.register(registerRequest);
            logger.info("Registration successful for username={}", registerRequest.getUsername());
            auditLogger.info("User {} registered successfully", registerRequest.getUsername());
            return ResponseEntity.ok(tokens);
        } catch (IllegalArgumentException e) {
            logger.warn("Registration failed for username={} - {}", registerRequest.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during registration for username={}", registerRequest.getUsername(), e);
            return ResponseEntity.internalServerError().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for username={}", loginRequest.getUsername());
        try {
            AuthResponse tokens = authService.login(loginRequest);
            logger.info("Login successful for username={}", loginRequest.getUsername());
            auditLogger.info("User {} logged in successfully", loginRequest.getUsername());
            return ResponseEntity.ok(tokens);
        } catch (IllegalArgumentException e) {
            logger.warn("Login failed for username={} - {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during login for username={}", loginRequest.getUsername(), e);
            return ResponseEntity.internalServerError().body("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest refreshRequest) {
        logger.info("Refresh token attempt");
        try {
            AuthResponse tokens = authService.refreshAccessToken(refreshRequest.getRefreshToken());
            logger.info("Access token refreshed successfully for userId={}", tokens.getUserId());
            auditLogger.info("User {} refreshed access token successfully", tokens.getUserName());
            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            logger.warn("Invalid or expired refresh token used");
            return ResponseEntity.status(401).body("Invalid or expired refresh token");
        }
    }
}
