package com.example.wordsearch.service;

import com.example.wordsearch.dto.LeaderboardEntry;
import com.example.wordsearch.model.User;
import com.example.wordsearch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;

    public List<LeaderboardEntry> getTop10() {

        Aggregation aggregation = newAggregation(

                match(Criteria.where("completed").is(true)),

                project()
                        .and("userId").as("userId")
                        .andExpression("size(foundWords)").as("wordsFound")
                        .and("timeTakenSeconds").as("timeTakenSeconds"),

                sort(Sort.by(
                        Sort.Order.desc("wordsFound"),
                        Sort.Order.asc("timeTakenSeconds")
                ))
        );

        var results =
                mongoTemplate.aggregate(
                        aggregation,
                        "game_sessions",
                        Document.class
                );

        List<LeaderboardEntry> leaderboard = new ArrayList<>();

        for (Document doc : results.getMappedResults()) {

            String userId = doc.getString("userId");

            User user = userRepository.findById(userId).orElse(null);

            leaderboard.add(
                    LeaderboardEntry.builder()
                            .username(user != null ?
                                    user.getUsername() : "Unknown")
                            .wordsFound(doc.getInteger("wordsFound"))
                            .timeTakenSeconds(
                                    doc.getLong("timeTakenSeconds"))
                            .build()
            );
        }

        return leaderboard;
    }
    public List<LeaderboardEntry> getAll() {

        Aggregation aggregation = newAggregation(

                project()
                        .and("userId").as("userId")
                        .andExpression("size(foundWords)").as("wordsFound")
                        .and("startTime").as("startTime")
                        .and("endTime").as("endTime")
                        .and("completed").as("completed")
                        .andExpression(
                                "toLong(divide(subtract(cond(completed, endTime, $$NOW), startTime), 1000))"
                        ).as("timeTakenSeconds"),

                sort(Sort.by(
                        Sort.Order.desc("wordsFound"),
                        Sort.Order.asc("timeTakenSeconds")
                ))
        );

        var results =
                mongoTemplate.aggregate(
                        aggregation,
                        "game_sessions",
                        Document.class
                );

        List<LeaderboardEntry> leaderboard = new ArrayList<>();

        for (Document doc : results.getMappedResults()) {

            String userId = doc.getString("userId");

            User user = userRepository.findById(userId).orElse(null);

            leaderboard.add(
                    LeaderboardEntry.builder()
                            .username(user != null ?
                                    user.getUsername() : "Unknown")
                            .wordsFound(doc.getInteger("wordsFound"))
                            .timeTakenSeconds(
                                    doc.getLong("timeTakenSeconds"))
                            .build()
            );
        }

        return leaderboard;
    }
}