package com.example.wordsearch.service;

import com.example.wordsearch.dto.AuthResponse;
import com.example.wordsearch.dto.ChangePasswordRequest;
import com.example.wordsearch.dto.LoginRequest;
import com.example.wordsearch.dto.RefreshRequest;
import com.example.wordsearch.model.RefreshToken;
import com.example.wordsearch.model.User;
import com.example.wordsearch.repository.GameSessionRepository;
import com.example.wordsearch.repository.RefreshTokenRepository;
import com.example.wordsearch.repository.UserRepository;
import com.example.wordsearch.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final GameSessionRepository gameSessionRepository;

    // ---------------- LOGIN ----------------

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (user.isAccountLocked()) {
            throw new LockedException("Account is locked");
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));
        } catch (BadCredentialsException ex) {
            incrementFailedAttempts(user);
            throw ex;
        }

        resetFailedAttempts(user);

        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRoles());

        String refreshTokenValue = jwtUtil.generateRefreshToken(user.getUsername());

        saveOrRotateRefreshToken(user, refreshTokenValue);

        log.info("User {} logged in successfully", user.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .requirePasswordChange(user.isMustChangePassword())
                .build();
    }

    // ---------------- REFRESH ----------------

    public AuthResponse refresh(RefreshRequest request) {

        if (!jwtUtil.isTokenValid(request.getRefreshToken())) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        String username = jwtUtil.extractUsername(request.getRefreshToken());

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        RefreshToken stored =
                refreshTokenRepository.findByUserId(user.getId())
                        .orElseThrow(() ->
                                new BadCredentialsException("Refresh token not found"));

        if (!stored.getToken().equals(request.getRefreshToken())) {
            throw new BadCredentialsException("Refresh token mismatch");
        }

        if (stored.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.deleteByUserId(user.getId());
            throw new BadCredentialsException("Refresh token expired");
        }

        // ROTATE
        String newRefresh = jwtUtil.generateRefreshToken(username);
        saveOrRotateRefreshToken(user, newRefresh);

        String newAccess =
                jwtUtil.generateAccessToken(username, user.getRoles());

        return AuthResponse.builder()
                .accessToken(newAccess)
                .refreshToken(newRefresh)
                .requirePasswordChange(user.isMustChangePassword())
                .build();
    }

    // ---------------- CHANGE PASSWORD ----------------

    public void changePassword(String username,ChangePasswordRequest request) {

        User user = userRepository.findByUsername(username).orElseThrow();

        if (!passwordEncoder.matches(request.getOldPassword(),user.getPassword())) {
            throw new BadCredentialsException("Invalid old password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setMustChangePassword(false);

        userRepository.save(user);
    }

    // ---------------- LOGOUT ----------------

    public void logout(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow();

        refreshTokenRepository.deleteByUserId(user.getId());

        gameSessionRepository.findByUserIdAndCompletedFalse(user.getId()).ifPresent(session -> {

            session.setEndTime(LocalDateTime.now());
            session.setLastActivityAt(LocalDateTime.now());
            long seconds = Duration.between(
                    session.getStartTime(),
                    session.getEndTime()
            ).getSeconds();

            session.setTimeTakenSeconds(seconds);

            gameSessionRepository.save(session);
        });

    }

    // ---------------- HELPERS ----------------

    private void incrementFailedAttempts(User user) {

        int attempts = user.getFailedLoginAttempts() + 1;

        user.setFailedLoginAttempts(attempts);

        if (attempts >= 5) {
            user.setAccountLocked(true);
            log.warn("User {} locked due to failed attempts",
                    user.getUsername());
        }

        userRepository.save(user);
    }

    private void resetFailedAttempts(User user) {

        user.setFailedLoginAttempts(0);
        user.setLastLoginAt(java.time.LocalDateTime.now());
        userRepository.save(user);
    }

    private void saveOrRotateRefreshToken(User user, String tokenValue) {

        Instant expiry = jwtUtil.extractExpiration(tokenValue);

        refreshTokenRepository.deleteByUserId(user.getId());

        RefreshToken token = RefreshToken.builder()
                .userId(user.getId())
                .token(tokenValue)
                .expiryDate(expiry)
                .build();

        refreshTokenRepository.save(token);
    }
}