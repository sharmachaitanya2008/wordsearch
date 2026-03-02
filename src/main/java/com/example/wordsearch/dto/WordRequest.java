package com.example.wordsearch.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WordRequest {
    @NotBlank
    private String value;
    @NotBlank
    private String clue;
}
