package com.example.wordsearch.controller;

import com.example.wordsearch.dto.CreateUserRequest;
import com.example.wordsearch.dto.ResetPasswordRequest;
import com.example.wordsearch.dto.UserResponse;
import com.example.wordsearch.service.AdminService;
import com.example.wordsearch.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        adminService.createUser(request),
                        MDC.get("correlationId")
                )
        );
    }

    @PutMapping("/{id}/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @PathVariable String id,
            @Valid @RequestBody ResetPasswordRequest request) {

        adminService.resetPassword(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Password reset successfully",
                        MDC.get("correlationId")
                )
        );
    }

    @PutMapping("/{id}/unlock")
    public ResponseEntity<ApiResponse<String>> unlockAccount(
            @PathVariable String id) {

        adminService.unlockAccount(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Account unlocked",
                        MDC.get("correlationId")
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> listUsers() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        adminService.listUsers(),
                        MDC.get("correlationId")
                )
        );
    }
}