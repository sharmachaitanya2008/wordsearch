package com.example.wordsearch.config;

import com.example.wordsearch.util.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.wordsearch.util.ErrorResponse.of;
import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.ResponseEntity.status;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {

        String correlationId = MDC.get("correlationId");
        ex.printStackTrace();
        log.error("|{}|Generic Error:",correlationId,ex);
        return status(BAD_REQUEST)
                .body(of("GENERIC_ERROR",ex.getMessage(),correlationId));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleValidation(BindException ex) {

        String correlationId = MDC.get("correlationId");
        log.error("|{}|Validation Error:",correlationId,ex);
        return status(BAD_REQUEST)
                .body(of("VALIDATION_ERROR", requireNonNull(ex.getFieldError()).getDefaultMessage(),correlationId));
    }
}