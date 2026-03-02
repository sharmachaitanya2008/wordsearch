package com.example.wordsearch.repository;

import com.example.wordsearch.model.Word;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WordRepository extends MongoRepository<Word, String> {
}