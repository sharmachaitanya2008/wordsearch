package com.example.wordsearch.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class UserResponse {

    private String id;
    private String username;
    private Set<String> roles;
    private boolean enabled;
    private boolean accountLocked;
    private boolean mustChangePassword;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}