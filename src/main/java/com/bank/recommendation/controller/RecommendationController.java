package com.bank.recommendation.controller;

import com.bank.recommendation.dto.RecommendationDto;
import com.bank.recommendation.service.RecommendationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    private static final Logger log = LoggerFactory.getLogger(RecommendationController.class);
    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getRecommendations(@PathVariable String userId) {
        log.info("Received request for user: {}", userId);

        UUID userUuid;
        try {
            userUuid = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID format: {}", userId);
            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("user_id", userId);
            errorResponse.put("recommendations", Collections.emptyList());
            errorResponse.put("error", "Invalid user ID format");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            List<RecommendationDto> recommendations = service.getRecommendations(userUuid);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("user_id", userId);
            response.put("recommendations", recommendations);

            log.info("Returning {} recommendations for user: {}", recommendations.size(), userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error processing request for user {}: {}", userId, e.getMessage(), e);

            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("user_id", userId);
            errorResponse.put("recommendations", Collections.emptyList());
            errorResponse.put("error", "Internal server error: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ДОПОЛНИТЕЛЬНЫЕ ЭНДПОИНТЫ

    // Очистить кэш
    @DeleteMapping("/cache")
    public ResponseEntity<String> clearCache() {
        service.clearCache();
        return ResponseEntity.ok("Cache cleared successfully");
    }

    // Получить информацию о сервисе
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("service", "Recommendation Service");
        info.put("version", "1.0.0");
        info.put("status", "running");
        info.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(info);
    }
}