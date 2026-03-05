package com.example.wordsearch.controller;

import com.example.wordsearch.dto.*;
import com.example.wordsearch.service.AuthService;
import com.example.wordsearch.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.wordsearch.util.ApiResponse.success;
import static org.slf4j.MDC.get;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);
        return ok(success(response, get("correlationId")));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshRequest request) {

        AuthResponse response = authService.refresh(request);
        return ok(success(response, get("correlationId")));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {

        authService.changePassword(authentication.getName(), request);
        return ok(success("Password changed successfully", get("correlationId")));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(Authentication authentication) {
        authService.logout(authentication.getName());
        return ok(success("Logged out successfully", get("correlationId")));
    }

    @PostMapping("/guest-login")
    public ResponseEntity<ApiResponse<AuthResponse>> guestLogin(@RequestBody GuestLoginRequest request) {
        return ok(success(authService.guestLogin(request.getUsername()), get("correlationId")));
    }
}