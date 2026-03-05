package com.example.wordsearch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.compare;

@Service
@Slf4j
public class GridGeneratorService {

    private static final int MAX_RETRIES = 1000;
    private final SecureRandom random = new SecureRandom();

    public Pair<Integer, List<List<String>>> generateGrid(List<String> words) {

        words.sort((a, b) -> compare(b.length(), a.length()));

        int size = words.get(0).length()+2;
        log.info("Longest Word:{}, size:{}",words.get(0),size);

        char[][] grid = new char[size][size];

        for (char[] row : grid)
            Arrays.fill(row, '\0');

        for (String word : words) {

            if (!placeWord(grid, word, size)) {
                throw new RuntimeException("Unable to place word: " + word);
            }
        }

        fillEmptyCells(grid);

        return Pair.of(size,convertToList(grid));
    }

    private boolean placeWord(char[][] grid,
                              String word,
                              int size) {

        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {

            Direction dir =
                    Direction.values()[random.nextInt(
                            Direction.values().length)];

            int row = random.nextInt(size);
            int col = random.nextInt(size);

            if (canPlace(grid, word, row, col, dir, size)) {

                for (int i = 0; i < word.length(); i++) {

                    int r = row + i * dir.dx;
                    int c = col + i * dir.dy;

                    grid[r][c] = word.charAt(i);
                }

                return true;
            }
        }

        return false;
    }

    private boolean canPlace(char[][] grid,
                             String word,
                             int row,
                             int col,
                             Direction dir,
                             int size) {

        for (int i = 0; i < word.length(); i++) {

            int r = row + i * dir.dx;
            int c = col + i * dir.dy;

            if (r < 0 || r >= size ||
                    c < 0 || c >= size)
                return false;

            char existing = grid[r][c];

            if (existing != '\0' &&
                    existing != word.charAt(i))
                return false;
        }

        return true;
    }

    private void fillEmptyCells(char[][] grid) {

        for (int i = 0; i < grid.length; i++) {

            for (int j = 0; j < grid.length; j++) {

                if (grid[i][j] == '\0') {

                    grid[i][j] =
                            (char) ('A' + random.nextInt(26));
                }
            }
        }
    }

    private List<List<String>> convertToList(char[][] grid) {

        List<List<String>> result = new ArrayList<>();

        for (char[] row : grid) {

            List<String> listRow = new ArrayList<>();

            for (char c : row) {
                listRow.add(String.valueOf(c));
            }

            result.add(listRow);
        }

        return result;
    }

    private enum Direction {
        HORIZONTAL_L2R(0, 1),
        HORIZONTAL_R2L(0, -1),
        VERTICAL_L2R(1, 0),
        VERTICAL_R2L(-1, 0),
        DIAGONAL_L2R_DOWN(1, 1),
        DIAGONAL_L2R_UP(-1, 1),
        DIAGONAL_R2L_DOWN(-1, -1),
        DIAGONAL_R2l_UP(1, -1);

        final int dx;
        final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }
}