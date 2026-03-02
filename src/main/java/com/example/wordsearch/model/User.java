package com.example.wordsearch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

import static java.time.LocalDateTime.now;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("users")
public class User {

    @Id
    private String id;
    @Indexed(unique = true)
    private String username;
    private String password;
    private Set<String> roles;
    @Builder.Default
    private boolean enabled = true;
    @Builder.Default
    private boolean mustChangePassword = true;
    @Builder.Default
    private int failedLoginAttempts = 0;
    @Builder.Default
    private boolean accountLocked = false;
    @Builder.Default
    private LocalDateTime createdAt = now();
    private LocalDateTime lastLoginAt;
}