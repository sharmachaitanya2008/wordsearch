package com.example.wordsearch.service;

import com.example.wordsearch.dto.CreateUserRequest;
import com.example.wordsearch.dto.ResetPasswordRequest;
import com.example.wordsearch.dto.UserResponse;
import com.example.wordsearch.model.User;
import com.example.wordsearch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // -------- CREATE USER --------

    public UserResponse createUser(CreateUserRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(request.getRoles())
                .mustChangePassword(true)
                .enabled(true)
                .accountLocked(false)
                .failedLoginAttempts(0)
                .build();

        userRepository.save(user);

        return mapToResponse(user);
    }

    // -------- RESET PASSWORD --------

    public void resetPassword(String userId,
                              ResetPasswordRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setMustChangePassword(true);

        userRepository.save(user);
    }

    // -------- UNLOCK ACCOUNT --------

    public void unlockAccount(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);

        userRepository.save(user);
    }

    // -------- LIST USERS --------

    public List<UserResponse> listUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // -------- MAPPER --------

    private UserResponse mapToResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .roles(user.getRoles())
                .enabled(user.isEnabled())
                .accountLocked(user.isAccountLocked())
                .mustChangePassword(user.isMustChangePassword())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}