package com.example.wordsearch.config;

import com.example.wordsearch.model.User;
import com.example.wordsearch.model.Word;
import com.example.wordsearch.repository.UserRepository;
import com.example.wordsearch.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader {

    private final UserRepository userRepository;
    private final WordRepository wordRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void loadData() {

        loadUsers();
        loadWords();
    }

    private void loadUsers() {

        if (userRepository.findByUsername("admin").isEmpty()) {

            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .roles(Set.of("ADMIN"))
                    .enabled(true)
                    .mustChangePassword(false)
                    .accountLocked(false)
                    .failedLoginAttempts(0)
                    .build();

            userRepository.save(admin);
        }

        if (userRepository.findByUsername("testuser").isEmpty()) {

            User user = User.builder()
                    .username("testuser")
                    .password(passwordEncoder.encode("user123"))
                    .roles(Set.of("USER"))
                    .enabled(true)
                    .mustChangePassword(true)
                    .accountLocked(false)
                    .failedLoginAttempts(0)
                    .build();

            userRepository.save(user);
        }
        if (userRepository.findByUsername("testuser2").isEmpty()) {

            User user = User.builder()
                    .username("testuser2")
                    .password(passwordEncoder.encode("user123"))
                    .roles(Set.of("USER"))
                    .enabled(true)
                    .mustChangePassword(true)
                    .accountLocked(false)
                    .failedLoginAttempts(0)
                    .build();

            userRepository.save(user);
        }
        if (userRepository.findByUsername("testuser3").isEmpty()) {

            User user = User.builder()
                    .username("testuser3")
                    .password(passwordEncoder.encode("user123"))
                    .roles(Set.of("USER"))
                    .enabled(true)
                    .mustChangePassword(true)
                    .accountLocked(false)
                    .failedLoginAttempts(0)
                    .build();

            userRepository.save(user);
        }
    }

    private void loadWords() {

        if (wordRepository.count() == 0) {

            List<Word> words = List.of(

                    Word.builder()
                            .value("SPRINGBOOT")
                            .clue("Popular Java framework")
                            .build(),

                    Word.builder()
                            .value("JAVA")
                            .clue("Programming language by Oracle")
                            .build(),

                    Word.builder()
                            .value("MONGODB")
                            .clue("NoSQL document database")
                            .build(),

                    Word.builder()
                            .value("SPRINGSECURITY")
                            .clue("Authentication framework")
                            .build(),

                    Word.builder()
                            .value("JWT")
                            .clue("Token-based authentication mechanism")
                            .build(),

                    Word.builder()
                            .value("GRID")
                            .clue("Matrix structure used in the puzzle")
                            .build(),

                    Word.builder()
                            .value("SERVICELAYER")
                            .clue("Business logic layer in architecture")
                            .build(),

                    Word.builder()
                            .value("RESTCONTROLLER")
                            .clue("Handles HTTP requests in Spring")
                            .build(),

                    Word.builder()
                            .value("DATAREPOSITORY")
                            .clue("Abstraction for database access")
                            .build(),

                    Word.builder()
                            .value("DATABASE")
                            .clue("Represents interaction with DB")
                            .build()
            );

            wordRepository.saveAll(words);
        }
    }
}