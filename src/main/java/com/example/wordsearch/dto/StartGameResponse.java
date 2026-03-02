package com.example.wordsearch.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class StartGameResponse {

    private String sessionId;
    private List<List<String>> grid;
    private Map<String, String> puzzleMap;
    private List<String> foundWords;
    private int totalAttempts;
    private int correctAttempts;
    private boolean completed;
    private LocalDateTime startTime;
}