package com.bank.recommendation.controller;

import com.bank.recommendation.dto.RuleListResponse;
import com.bank.recommendation.dto.RuleRequest;
import com.bank.recommendation.dto.RuleResponse;
import com.bank.recommendation.entity.RuleEntity;
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

    // POST /rule - создание нового правила

    @PostMapping
    public ResponseEntity<RuleResponse> createRule(@RequestBody RuleRequest request) {
        log.info("Creating new dynamic rule for product: {}", request.getProductName());

        try {
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

        } catch (Exception e) {
            log.error("Error creating rule: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /rule - получение всех правил

    @GetMapping
    public ResponseEntity<RuleListResponse> getAllRules() {
        log.info("Getting all dynamic rules");

        try {
            List<RuleEntity> rules = dynamicRuleService.getAllRules();

            List<RuleResponse> responses = rules.stream()
                    .map(rule -> {
                        // Конвертируем QueryEntity обратно в QueryDto
                        List<com.bank.recommendation.dto.QueryDto> queryDtos = rule.getQueries().stream()
                                .map(q -> new com.bank.recommendation.dto.QueryDto(
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE /rule/{id} - удаление правила

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable UUID id) {
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}