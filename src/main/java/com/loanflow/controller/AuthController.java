package com.loanflow.controller;

import com.loanflow.dto.request.LoginRequest;
import com.loanflow.dto.request.RegisterRequest;
import com.loanflow.dto.response.ApiResponse;
import com.loanflow.dto.response.AuthResponse;
import com.loanflow.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    // Public endpoint to register a new user (Borrower or Loan Officer)
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        log.info("Received registration request for email: {}", request.getEmail());

        AuthResponse response = authService.register(request);

        // Returning 201 Created because a new User entity is saved to the database
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("User registered successfully.", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        log.info("Received login request for email: {}", request.getEmail());

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(ApiResponse.ok("Login successful.", response));
    }
}