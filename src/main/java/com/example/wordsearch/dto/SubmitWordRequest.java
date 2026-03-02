package com.example.wordsearch.dto;

import lombok.Data;

import java.util.List;

@Data
public class SubmitWordRequest {

    private List<Coordinate> coordinates;
}