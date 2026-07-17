package com.bank.recommendation.service;

import com.bank.recommendation.dto.RecommendationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecommendationService {

    private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);

    private final List<RecommendationRuleSet> staticRuleSets;
    private final DynamicRuleService dynamicRuleService;

    public RecommendationService(List<RecommendationRuleSet> staticRuleSets,
                                 DynamicRuleService dynamicRuleService) {
        this.staticRuleSets = staticRuleSets;
        this.dynamicRuleService = dynamicRuleService;
        log.info("Initialized RecommendationService with {} static rule sets", staticRuleSets.size());
    }

    public List<RecommendationDto> getRecommendations(UUID userId) {
        log.info("Getting recommendations for user: {}", userId);

        List<RecommendationDto> recommendations = new ArrayList<>();

        // 1. СТАТИЧЕСКИЕ правила
        for (RecommendationRuleSet rule : staticRuleSets) {
            try {
                rule.check(userId).ifPresent(recommendations::add);
            } catch (Exception e) {
                log.error("Error checking static rule {} for user {}: {}",
                        rule.getClass().getSimpleName(), userId, e.getMessage(), e);
            }
        }

        // 2. ДИНАМИЧЕСКИЕ правила
        try {
            List<RecommendationDto> dynamicRecommendations = dynamicRuleService.checkDynamicRules(userId);
            recommendations.addAll(dynamicRecommendations);
            log.debug("Added {} dynamic recommendations", dynamicRecommendations.size());
        } catch (Exception e) {
            log.error("Error checking dynamic rules for user {}: {}", userId, e.getMessage(), e);
        }

        log.info("Total recommendations found for user {}: {}", userId, recommendations.size());
        return recommendations;
    }
}