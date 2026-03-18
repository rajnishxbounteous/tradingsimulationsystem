package com.example.tradingsimulationsystem.controller;

import com.example.tradingsimulationsystem.dto.LoginRequest;
import com.example.tradingsimulationsystem.dto.RegisterRequest;
import com.example.tradingsimulationsystem.dto.AuthResponse;
import com.example.tradingsimulationsystem.dto.RefreshRequest;
import com.example.tradingsimulationsystem.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            AuthResponse tokens = authService.register(registerRequest);
            return ResponseEntity.ok(tokens);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println(loginRequest.getUsername());
            AuthResponse tokens = authService.login(loginRequest);
            return ResponseEntity.ok(tokens);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest refreshRequest) {
        try {
            AuthResponse tokens = authService.refreshAccessToken(refreshRequest.getRefreshToken());
            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid or expired refresh token");
        }
    }
}
