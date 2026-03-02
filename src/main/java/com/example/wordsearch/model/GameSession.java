package com.example.wordsearch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("game_sessions")
public class GameSession {
    @Id
    private String id;
    private String userId;
    private int gridSize;
    private List<List<String>> grid;
    private Map<String, String> puzzleMap;
    @Builder.Default
    private List<String> foundWords = new ArrayList<>();
    private int totalAttempts;
    private int correctAttempts;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean completed;
    private Long timeTakenSeconds;
    private LocalDateTime lastActivityAt;
}