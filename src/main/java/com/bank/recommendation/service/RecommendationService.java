package com.bank.recommendation.service;

import com.bank.recommendation.dto.RecommendationDto;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> ruleSets;

    public RecommendationService(List<RecommendationRuleSet> ruleSets) {
        this.ruleSets = ruleSets;
    }

    public List<RecommendationDto> getRecommendations(UUID userId) {
        List<RecommendationDto> recommendations = new ArrayList<>();

        for (RecommendationRuleSet rule : ruleSets) {
            Optional<RecommendationDto> result = rule.check(userId);
            result.ifPresent(recommendations::add);
        }

        return recommendations;
    }
}