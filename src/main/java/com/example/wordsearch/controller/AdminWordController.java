package com.example.wordsearch.controller;

import com.example.wordsearch.dto.WordRequest;
import com.example.wordsearch.model.Word;
import com.example.wordsearch.repository.WordRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/words")
@RequiredArgsConstructor
public class AdminWordController {

    private final WordRepository wordRepository;

    @GetMapping
    public List<Word> getAll() {
        return wordRepository.findAll();
    }

    @PostMapping
    public Word create(@RequestBody @Valid WordRequest request) {

        Word word = Word.builder()
                .value(request.getValue())
                .clue(request.getClue())
                .build();

        return wordRepository.save(word);
    }

    @PutMapping("/{id}")
    public Word update(@PathVariable String id,
                       @RequestBody @Valid WordRequest request) {

        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Word not found"));

        word.setValue(request.getValue());
        word.setClue(request.getClue());

        return wordRepository.save(word);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        wordRepository.deleteById(id);
    }
}
