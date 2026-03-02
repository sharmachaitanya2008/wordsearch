package com.example.wordsearch.controller;

import com.example.wordsearch.dto.AnalyticsResponse;
import com.example.wordsearch.service.AnalyticsService;
import com.example.wordsearch.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.wordsearch.util.ApiResponse.success;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getAnalytics() {

        return ok(success(analyticsService.getAnalytics(),MDC.get("correlationId")));
    }
}