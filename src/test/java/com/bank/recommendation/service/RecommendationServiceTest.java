package com.bank.recommendation.service;

import com.bank.recommendation.dto.RecommendationDto;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RecommendationServiceTest {

    private static final Logger log = LoggerFactory.getLogger(RecommendationServiceTest.class);

    @Autowired
    private RecommendationService service;

    // Тестовые пользователи из ТЗ
    private static final UUID INVEST_USER = UUID.fromString("cd515076-5d8a-44be-930e-8d4fcb79f42d");
    private static final UUID SAVING_USER = UUID.fromString("d4a4d619-9a0c-4fc5-b0cb-76c49409546b");
    private static final UUID CREDIT_USER = UUID.fromString("1f9b149c-6577-448a-bc94-16bea229b71a");

    @Test
    public void testInvestRecommendation() {
        log.info("Testing Invest recommendation for user: {}", INVEST_USER);

        List<RecommendationDto> recommendations = service.getRecommendations(INVEST_USER);

        boolean hasInvest = recommendations.stream()
                .anyMatch(r -> "Invest 500".equals(r.getName()));

        assertTrue(hasInvest, "User should get Invest 500 recommendation");

        // Выводим все рекомендации для отладки
        recommendations.forEach(r -> log.info("Recommendation: {}", r.getName()));
    }

    @Test
    public void testSavingRecommendation() {
        log.info("Testing Saving recommendation for user: {}", SAVING_USER);

        List<RecommendationDto> recommendations = service.getRecommendations(SAVING_USER);

        boolean hasSaving = recommendations.stream()
                .anyMatch(r -> "Top Saving".equals(r.getName()));

        assertTrue(hasSaving, "User should get Top Saving recommendation");
    }

    @Test
    public void testCreditRecommendation() {
        log.info("Testing Credit recommendation for user: {}", CREDIT_USER);

        List<RecommendationDto> recommendations = service.getRecommendations(CREDIT_USER);

        boolean hasCredit = recommendations.stream()
                .anyMatch(r -> "Простой кредит".equals(r.getName()));

        assertTrue(hasCredit, "User should get Credit recommendation");
    }

    @Test
    public void testUserWithoutRecommendations() {
        // Берем случайный UUID, которого нет в БД
        UUID randomUser = UUID.randomUUID();
        log.info("Testing user without recommendations: {}", randomUser);

        List<RecommendationDto> recommendations = service.getRecommendations(randomUser);

        assertTrue(recommendations.isEmpty(), "User should have no recommendations");
    }

    @Test
    public void testCacheWorks() {
        UUID testUser = INVEST_USER;

        // Первый запрос - должен вычислить
        long start1 = System.currentTimeMillis();
        List<RecommendationDto> result1 = service.getRecommendations(testUser);
        long time1 = System.currentTimeMillis() - start1;

        // Второй запрос - должен взять из кэша
        long start2 = System.currentTimeMillis();
        List<RecommendationDto> result2 = service.getRecommendations(testUser);
        long time2 = System.currentTimeMillis() - start2;

        // Проверяем, что кэш работает (второй запрос быстрее)
        assertTrue(time2 <= time1, "Cache should make second request faster");
        assertEquals(result1.size(), result2.size(), "Results should be same");

        log.info("First request: {}ms, Second request: {}ms", time1, time2);
    }
}