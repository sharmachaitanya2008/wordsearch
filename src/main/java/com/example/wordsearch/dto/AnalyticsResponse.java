package com.example.wordsearch.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnalyticsResponse {

    private long totalUsers;
    private long activeUsersLast7Days;
    private long lockedAccounts;
    private long totalGames;
    private long completedGames;
    private double averageWordsFound;
    private double averageCompletionTimeSeconds;
    private double averageAccuracy;
}