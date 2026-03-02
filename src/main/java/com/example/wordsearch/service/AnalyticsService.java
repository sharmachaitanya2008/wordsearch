package com.example.wordsearch.service;

import com.example.wordsearch.dto.AnalyticsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final MongoTemplate mongoTemplate;

    public AnalyticsResponse getAnalytics() {

        long totalUsers =
                mongoTemplate.getCollection("users").countDocuments();

        long activeUsers =
                mongoTemplate.getCollection("users")
                        .countDocuments(
                                new org.bson.Document("lastLoginAt",
                                        new org.bson.Document("$gte",
                                                LocalDateTime.now().minusDays(7)))
                        );

        long lockedAccounts =
                mongoTemplate.getCollection("users")
                        .countDocuments(
                                new org.bson.Document("accountLocked", true)
                        );

        long totalGames =
                mongoTemplate.getCollection("game_sessions")
                        .countDocuments();

        long completedGames =
                mongoTemplate.getCollection("game_sessions")
                        .countDocuments(
                                new org.bson.Document("completed", true)
                        );

        // -------- Average Words Found --------

        Aggregation wordsAgg = newAggregation(
                match(Criteria.where("completed").is(true)),
                project()
                        .andExpression("size(foundWords)").as("wordsCount"),
                group().avg("wordsCount").as("avgWords")
        );

        Double avgWords =
                extractSingleDouble(wordsAgg, "avgWords");

        // -------- Average Completion Time --------

        Aggregation timeAgg = newAggregation(
                match(Criteria.where("completed").is(true)),
                group().avg("timeTakenSeconds").as("avgTime")
        );

        Double avgTime =
                extractSingleDouble(timeAgg, "avgTime");

        // -------- Average Accuracy --------

        Aggregation accuracyAgg = newAggregation(
                match(Criteria.where("completed").is(true)),
                group()
                        .sum("correctAttempts").as("correct")
                        .sum("totalAttempts").as("total"),
                project()
                        .andExpression("correct / total").as("accuracy")
        );

        Double avgAccuracy =
                extractSingleDouble(accuracyAgg, "accuracy");

        return AnalyticsResponse.builder()
                .totalUsers(totalUsers)
                .activeUsersLast7Days(activeUsers)
                .lockedAccounts(lockedAccounts)
                .totalGames(totalGames)
                .completedGames(completedGames)
                .averageWordsFound(avgWords == null ? 0 : avgWords)
                .averageCompletionTimeSeconds(avgTime == null ? 0 : avgTime)
                .averageAccuracy(avgAccuracy == null ? 0 : avgAccuracy)
                .build();
    }

    private Double extractSingleDouble(Aggregation agg, String field) {

        AggregationResults<org.bson.Document> results =
                mongoTemplate.aggregate(agg,
                        "game_sessions",
                        org.bson.Document.class);

        List<org.bson.Document> mapped = results.getMappedResults();

        if (mapped.isEmpty()) return 0.0;

        Object value = mapped.get(0).get(field);

        return value == null ? 0.0 :
                ((Number) value).doubleValue();
    }
}