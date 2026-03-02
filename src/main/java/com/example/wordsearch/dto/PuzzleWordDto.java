package com.example.wordsearch.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PuzzleWordDto {
    private String clue;
    private String normalized; // for matching
}
