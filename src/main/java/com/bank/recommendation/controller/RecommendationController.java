package com.bank.recommendation.controller;

import com.bank.recommendation.dto.RecommendationDto;
import com.bank.recommendation.dto.RecommendationResponse;
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
    public ResponseEntity<RecommendationResponse> getRecommendations(@PathVariable String userId) {
        log.info("Received request for user: {}", userId);

        UUID userUuid;
        try {
            userUuid = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID format: {}", userId);
            return ResponseEntity.badRequest()
                    .body(new RecommendationResponse(userId, Collections.emptyList()));
        }

        try {
            List<RecommendationDto> recommendations = service.getRecommendations(userUuid);
            log.info("Returning {} recommendations for user: {}", recommendations.size(), userId);
            return ResponseEntity.ok(new RecommendationResponse(userId, recommendations));

        } catch (Exception e) {
            log.error("Error processing request for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RecommendationResponse(userId, Collections.emptyList()));
        }
    }

    // ДОПОЛНИТЕЛЬНЫЕ ЭНДПОИНТЫ (для отладки)


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