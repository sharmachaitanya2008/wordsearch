package com.example.wordsearch.controller;

import com.example.wordsearch.dto.StartGameResponse;
import com.example.wordsearch.dto.SubmitWordRequest;
import com.example.wordsearch.dto.SubmitWordResponse;
import com.example.wordsearch.service.GameService;
import com.example.wordsearch.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.example.wordsearch.util.ApiResponse.success;
import static org.slf4j.MDC.get;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class GameController {

    public static final String CORRELATION_ID = "correlationId";
    private final GameService gameService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<StartGameResponse>> start(Authentication authentication) {

        var response = gameService.startOrResume(authentication.getName());
        return ok(success(response,get(CORRELATION_ID)));
    }

    @PostMapping("/{sessionId}/word")
    public ResponseEntity<ApiResponse<SubmitWordResponse>> submitWord(
            Authentication authentication,
            @PathVariable String sessionId,
            @Valid @RequestBody SubmitWordRequest request) {

        var response = gameService.submitWord(authentication.getName(),sessionId,request);
        return ok(success(response, get(CORRELATION_ID)));
    }
    @GetMapping("/time")
    public ResponseEntity<ApiResponse<Long>> getTime(Authentication authentication) {
        String username = authentication.getName();
        return ok(success(gameService.getElapsedTime(authentication.getName()), get(CORRELATION_ID)));
    }
}