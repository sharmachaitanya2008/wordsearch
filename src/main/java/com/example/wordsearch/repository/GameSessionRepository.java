package com.example.wordsearch.repository;

import com.example.wordsearch.model.GameSession;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface GameSessionRepository
        extends MongoRepository<GameSession, String> {

    Optional<GameSession> findByUserIdAndCompletedFalse(String userId);
    Optional<GameSession> findTopByUserIdOrderByStartTimeDesc(String userId);
}