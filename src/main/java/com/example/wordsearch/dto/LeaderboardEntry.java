package com.example.wordsearch.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaderboardEntry {
    private String username;
    private Integer wordsFound;
    private Long timeTakenSeconds;
}