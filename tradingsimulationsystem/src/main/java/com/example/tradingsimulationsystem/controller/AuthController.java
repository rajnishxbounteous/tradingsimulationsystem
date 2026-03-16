//package com.example.tradingsimulationsystem.controller;
//
//import com.example.tradingsimulationsystem.dto.LoginRequest;
//import com.example.tradingsimulationsystem.dto.RegisterRequest;
//import com.example.tradingsimulationsystem.dto.AuthResponse;
//import com.example.tradingsimulationsystem.service.AuthService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//
//    private final AuthService authService;
//
//    public AuthController(AuthService authService) {
//        this.authService = authService;
//    }
//
//    /**
//     * Register a new user.
//     * Example: POST /api/auth/register
//     */
//    @PostMapping("/register")
//    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
//        try {
//            String token = authService.register(registerRequest);
//            return ResponseEntity.ok(new AuthResponse(token));
//        } catch (IllegalArgumentException e) {
//            // e.g. username already exists
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            // unexpected errors
//            return ResponseEntity.internalServerError().body("Registration failed: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Login and get JWT token.
//     * Example: POST /api/auth/login
//     */
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
//        try {
//            String token = authService.login(loginRequest);
//            return ResponseEntity.ok(new AuthResponse(token));
//        } catch (IllegalArgumentException e) {
//            // invalid credentials
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body("Login failed: " + e.getMessage());
//        }
//    }
//}
package com.example.tradingsimulationsystem.controller;

import com.example.tradingsimulationsystem.dto.LoginRequest;
import com.example.tradingsimulationsystem.dto.RegisterRequest;
import com.example.tradingsimulationsystem.dto.AuthResponse;
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

    /**
     * Register a new user.
     * Example: POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            // AuthService will set defaults for balance, marginAllowed, marginUsed, role
            String token = authService.register(registerRequest);
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Login and get JWT token.
     * Example: POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            String token = authService.login(loginRequest);
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Login failed: " + e.getMessage());
        }
    }
}
