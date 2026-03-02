package com.example.wordsearch.service;

import com.example.wordsearch.dto.*;
import com.example.wordsearch.model.GameSession;
import com.example.wordsearch.model.User;
import com.example.wordsearch.model.Word;
import com.example.wordsearch.repository.GameSessionRepository;
import com.example.wordsearch.repository.UserRepository;
import com.example.wordsearch.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.time.Duration.between;
import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("java:S112")
public class GameService {

    private static final long IDLE_TIMEOUT_MINUTES = 30;
    public static final String USER_NOT_FOUND = "User not found";

    private final GameSessionRepository sessionRepository;
    private final WordRepository wordRepository;
    private final UserRepository userRepository;
    private final GridGeneratorService gridGeneratorService;

    // ===============================
    // START OR RESUME GAME
    // ===============================

    public StartGameResponse startOrResume(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));

        // Check active session
        var active =
                sessionRepository.findByUserIdAndCompletedFalse(user.getId());

        if (active.isPresent()) {
            return mapToStartResponse(active.get());
        }

        // Fetch latest session (even if completed)
        var latest = sessionRepository.findTopByUserIdOrderByStartTimeDesc(user.getId());

        if (latest.isPresent()) {
            return mapToStartResponse(latest.get());
        }

        // If no session exists at all → create first one
        return createNewSession(user);
    }

    // ===============================
    // SUBMIT WORD (COORDINATE BASED)
    // ===============================

    public SubmitWordResponse submitWord(String username,
                                         String sessionId,
                                         SubmitWordRequest request) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(GameService.USER_NOT_FOUND));

        GameSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getUserId().equals(user.getId())) {
            throw new RuntimeException("Invalid session ownership");
        }

        if (session.isCompleted()) {
            throw new RuntimeException("Game already completed");
        }
        checkAndHandleIdleTimeout(session);

        session.setTotalAttempts(session.getTotalAttempts() + 1);

        String reconstructed = reconstructWord(session, request.getCoordinates());

        boolean valid = session.getPuzzleMap().containsKey(reconstructed)
                        && !session.getFoundWords().contains(reconstructed);

        if (valid) {
            session.getFoundWords().add(reconstructed);
            session.setCorrectAttempts(session.getCorrectAttempts() + 1);
        }

        // -------- COMPLETION CHECK --------

        if (session.getFoundWords().size() == session.getPuzzleMap().size()) {
            session.setCompleted(true);
            session.setEndTime(now());
            long seconds = between(session.getStartTime(),session.getEndTime()).getSeconds();
            session.setTimeTakenSeconds(seconds);
        }
        session.setLastActivityAt(now());
        sessionRepository.save(session);

        return mapToSubmitResponse(session, valid);
    }

    // ===============================
    // RESPONSE MAPPERS
    // ===============================

    private StartGameResponse mapToStartResponse(GameSession session) {

        return StartGameResponse.builder()
                .sessionId(session.getId())
                .grid(session.getGrid())
                .puzzleMap(session.getPuzzleMap())
                .foundWords(session.getFoundWords())
                .totalAttempts(session.getTotalAttempts())
                .correctAttempts(session.getCorrectAttempts())
                .completed(session.isCompleted())
                .startTime(session.getStartTime())
                .build();
    }

    private SubmitWordResponse mapToSubmitResponse(GameSession session,boolean valid) {

        double accuracy = 0d;

        if (session.getTotalAttempts() > 0) {
            accuracy = (double) session.getCorrectAttempts()
                    / session.getTotalAttempts();
        }

        return SubmitWordResponse.builder()
                .valid(valid)
                .completed(session.isCompleted())
                .foundWords(session.getFoundWords())
                .totalAttempts(session.getTotalAttempts())
                .correctAttempts(session.getCorrectAttempts())
                .timeTakenSeconds(
                        session.isCompleted()
                                ? session.getTimeTakenSeconds()
                                : null
                )
                .accuracy(accuracy)
                .build();
    }

    // ===============================
    // WORD RECONSTRUCTION & VALIDATION
    // ===============================

    private String reconstructWord(GameSession session,List<Coordinate> coords) {

        if (coords == null || coords.isEmpty())
            return "";

        validatePath(coords);

        List<List<String>> grid = session.getGrid();
        StringBuilder word = new StringBuilder();

        for (Coordinate c : coords) {

            if (c.getRow() < 0 || c.getCol() < 0
                    || c.getRow() >= session.getGridSize()
                    || c.getCol() >= session.getGridSize()) {
                return "";
            }

            word.append(grid.get(c.getRow())
                    .get(c.getCol()));
        }

        return word.toString();
    }

    private void validatePath(List<Coordinate> coords) {

        if (coords.size() < 2) return;

        int dr = coords.get(1).getRow()
                - coords.get(0).getRow();
        int dc = coords.get(1).getCol()
                - coords.get(0).getCol();

        // must move at most 1 cell in each direction
        if (!(Math.abs(dr) <= 1 && Math.abs(dc) <= 1)) {
            return;
        }

        for (int i = 1; i < coords.size(); i++) {

            int curDr = coords.get(i).getRow() - coords.get(i - 1).getRow();
            int curDc = coords.get(i).getCol() - coords.get(i - 1).getCol();

            if (curDr != dr || curDc != dc) {
                return;
            }

            if (!(Math.abs(curDr) <= 1 && Math.abs(curDc) <= 1)) {
                return;
            }
        }
    }
    private StartGameResponse createNewSession(User user) {

        List<Word> dbWords = wordRepository.findAll();

        Map<String, String> puzzleMap = new LinkedHashMap<>();

        for (Word w : dbWords) {
            String normalized = normalize(w.getValue());
            puzzleMap.put(normalized, w.getClue());
        }

        // IMPORTANT: clone keys for grid placement
        List<String> wordsForGrid = new ArrayList<>(puzzleMap.keySet());


        var grid =
                gridGeneratorService.generateGrid(wordsForGrid);

        GameSession session = GameSession.builder()
                .userId(user.getId())
                .gridSize(grid.getFirst())
                .grid(grid.getSecond())
                .puzzleMap(puzzleMap)
                .foundWords(new ArrayList<>())
                .totalAttempts(0)
                .correctAttempts(0)
                .startTime(now())
                .completed(false)
                .lastActivityAt(now())
                .build();

        sessionRepository.save(session);

        return mapToStartResponse(session);
    }
    public long getElapsedTime(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(GameService.USER_NOT_FOUND));

        GameSession session = sessionRepository
                .findTopByUserIdOrderByStartTimeDesc(user.getId())
                .orElseThrow(() -> new RuntimeException("No active session"));

        checkAndHandleIdleTimeout(session);

        if (session.isCompleted()) {
            return session.getTimeTakenSeconds();
        }
        return between(session.getStartTime(),now()).getSeconds();
    }
    private void checkAndHandleIdleTimeout(GameSession session) {

        if (session.getEndTime() != null) {
            return; // already closed
        }
        LocalDateTime currentTime = now();
        long idleMinutes = between(session.getLastActivityAt(), currentTime).toMinutes();

        if (idleMinutes >= IDLE_TIMEOUT_MINUTES) {

            session.setEndTime(currentTime);
            long seconds = between(session.getStartTime(), currentTime).getSeconds();
            session.setTimeTakenSeconds(seconds);
            sessionRepository.save(session);
        }
    }
    private String normalize(String value) {
        return value
                .replaceAll("[^A-Za-z]", "")  // remove spaces, hyphens, etc.
                .toUpperCase();
    }
}