package com.bank.recommendation.service;

import com.bank.recommendation.dto.RecommendationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RecommendationService {

    private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);

    private final List<RecommendationRuleSet> ruleSets;

    // Простой кэш для хранения результатов
    private final Map<UUID, List<RecommendationDto>> cache = new ConcurrentHashMap<>();

    public RecommendationService(List<RecommendationRuleSet> ruleSets) {
        this.ruleSets = ruleSets;
        log.info("Initialized RecommendationService with {} rule sets", ruleSets.size());
    }

    public List<RecommendationDto> getRecommendations(UUID userId) {
        log.info("Getting recommendations for user: {}", userId);

        // Проверка кэша
        if (cache.containsKey(userId)) {
            log.debug("Returning cached recommendations for user: {}", userId);
            return cache.get(userId);
        }

        // Вычисление рекомендаций
        List<RecommendationDto> recommendations = new ArrayList<>();

        for (RecommendationRuleSet rule : ruleSets) {
            try {
                Optional<RecommendationDto> result = rule.check(userId);
                result.ifPresent(recommendations::add);
            } catch (Exception e) {
                log.error("Error checking rule {} for user {}: {}",
                        rule.getClass().getSimpleName(), userId, e.getMessage(), e);
            }
        }

        log.info("Found {} recommendations for user: {}", recommendations.size(), userId);

        // Сохранение в кэш
        cache.put(userId, recommendations);

        return recommendations;
    }

    // Метод для очистки кэша
    public void clearCache() {
        cache.clear();
        log.info("Cache cleared");
    }

    // Метод для очистки кэша для конкретного пользователя
    public void clearCacheForUser(UUID userId) {
        cache.remove(userId);
        log.info("Cache cleared for user: {}", userId);
    }
}