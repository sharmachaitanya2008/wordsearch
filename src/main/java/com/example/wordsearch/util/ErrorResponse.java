package com.example.wordsearch.util;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ErrorResponse {

    private boolean success;
    private ErrorDetail error;
    private Instant timestamp;
    private String correlationId;

    public static ErrorResponse of(String code, String message, String correlationId) {
        return ErrorResponse.builder()
                .success(false)
                .error(ErrorDetail.builder()
                        .code(code)
                        .message(message)
                        .build())
                .timestamp(Instant.now())
                .correlationId(correlationId)
                .build();
    }

    @Data
    @Builder
    public static class ErrorDetail {
        private String code;
        private String message;
    }
}