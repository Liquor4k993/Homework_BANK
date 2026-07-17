package com.bank.recommendation.controller;

import com.bank.recommendation.dto.*;
import com.bank.recommendation.entity.RuleEntity;
import com.bank.recommendation.entity.RuleStatEntity;
import com.bank.recommendation.service.DynamicRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/rule")
public class RuleController {

    private static final Logger log = LoggerFactory.getLogger(RuleController.class);
    private final DynamicRuleService dynamicRuleService;

    public RuleController(DynamicRuleService dynamicRuleService) {
        this.dynamicRuleService = dynamicRuleService;
    }

    @PostMapping
    public ResponseEntity<?> createRule(@RequestBody RuleRequest request) {
        log.info("Creating new dynamic rule for product: {}", request.getProductName());

        try {
            validateRuleRequest(request);

            RuleEntity saved = dynamicRuleService.createRule(
                    request.getProductName(),
                    request.getProductId(),
                    request.getProductText(),
                    request.getRule()
            );

            RuleResponse response = new RuleResponse(
                    saved.getId(),
                    saved.getProductName(),
                    saved.getProductId(),
                    saved.getProductText(),
                    request.getRule()
            );

            log.info("Created rule with id: {}", saved.getId());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating rule: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllRules() {
        log.info("Getting all dynamic rules");

        try {
            List<RuleEntity> rules = dynamicRuleService.getAllRules();

            List<RuleResponse> responses = rules.stream()
                    .map(rule -> {
                        List<QueryDto> queryDtos = rule.getQueries().stream()
                                .map(q -> new QueryDto(
                                        q.getQueryType(),
                                        q.getArguments(),
                                        q.isNegate()
                                ))
                                .toList();

                        return new RuleResponse(
                                rule.getId(),
                                rule.getProductName(),
                                rule.getProductId(),
                                rule.getProductText(),
                                queryDtos
                        );
                    })
                    .toList();

            return ResponseEntity.ok(new RuleListResponse(responses));

        } catch (Exception e) {
            log.error("Error getting rules: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRule(@PathVariable UUID id) {
        log.info("Deleting rule with id: {}", id);

        try {
            dynamicRuleService.deleteRule(id);
            log.info("Deleted rule with id: {}", id);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            log.warn("Rule not found with id: {}", id);
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("Error deleting rule: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        log.info("Getting rule statistics");

        try {
            List<RuleEntity> allRules = dynamicRuleService.getAllRules();
            List<RuleStatEntity> stats = dynamicRuleService.getAllStats();

            Map<UUID, Long> statMap = new HashMap<>();
            for (RuleStatEntity stat : stats) {
                statMap.put(stat.getRule().getId(), stat.getCount());
            }

            List<RuleStatsResponse.RuleStat> result = new ArrayList<>();
            for (RuleEntity rule : allRules) {
                long count = statMap.getOrDefault(rule.getId(), 0L);
                result.add(new RuleStatsResponse.RuleStat(rule.getId(), count));
            }

            return ResponseEntity.ok(new RuleStatsResponse(result));

        } catch (Exception e) {
            log.error("Error getting rule stats: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    /**
     * Валидация запроса на создание правила
     */
    private void validateRuleRequest(RuleRequest request) {
        if (request.getProductName() == null || request.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (request.getProductId() == null || request.getProductId().trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID is required");
        }
        if (request.getRule() == null || request.getRule().isEmpty()) {
            throw new IllegalArgumentException("At least one query is required");
        }
    }
}