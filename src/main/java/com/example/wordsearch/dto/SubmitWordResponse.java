package com.example.wordsearch.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SubmitWordResponse {

    private boolean valid;
    private boolean completed;
    private List<String> foundWords;
    private int totalAttempts;
    private int correctAttempts;
    private Long timeTakenSeconds;
    private Double accuracy;
}