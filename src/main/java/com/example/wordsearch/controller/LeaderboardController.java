package com.example.wordsearch.controller;

import com.example.wordsearch.dto.LeaderboardEntry;
import com.example.wordsearch.service.LeaderboardService;
import com.example.wordsearch.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<LeaderboardEntry>>> getLeaderboard() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        leaderboardService.getTop10(),
                        MDC.get("correlationId")
                )
        );
    }
}