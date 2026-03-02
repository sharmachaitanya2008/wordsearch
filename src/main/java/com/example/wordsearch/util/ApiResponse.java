package com.example.wordsearch.util;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private Instant timestamp;
    private String correlationId;

    public static <T> ApiResponse<T> success(T data, String correlationId) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(Instant.now())
                .correlationId(correlationId)
                .build();
    }
}