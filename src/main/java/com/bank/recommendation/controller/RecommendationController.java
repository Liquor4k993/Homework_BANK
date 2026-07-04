package com.bank.recommendation.controller;

import com.bank.recommendation.dto.RecommendationDto;
import com.bank.recommendation.service.RecommendationService;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    @GetMapping("/{userId}")
    public Map<String, Object> getRecommendations(@PathVariable String userId) {
        UUID userUuid;
        try {
            userUuid = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            // Если невалидный UUID - возвращаем пустой ответ
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("user_id", userId);
            errorResponse.put("recommendations", Collections.emptyList());
            return errorResponse;
        }

        List<RecommendationDto> recommendations = service.getRecommendations(userUuid);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("user_id", userId);
        response.put("recommendations", recommendations);

        return response;
    }
}