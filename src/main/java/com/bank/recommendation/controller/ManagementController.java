package com.bank.recommendation.controller;

import com.bank.recommendation.repository.UserStatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/management")
public class ManagementController {

    private static final Logger log = LoggerFactory.getLogger(ManagementController.class);

    private final UserStatsRepository userStatsRepository;

    @Value("${spring.application.name:Star Bank Recommendation Service}")
    private String serviceName;

    @Value("${application.version:1.0.0}")
    private String serviceVersion;

    public ManagementController(UserStatsRepository userStatsRepository) {
        this.userStatsRepository = userStatsRepository;
    }

    // ============================================
    // GET /management/info
    // ============================================
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getInfo() {
        log.info("Getting service info");

        Map<String, String> info = new LinkedHashMap<>();
        info.put("name", serviceName);
        info.put("version", serviceVersion);

        return ResponseEntity.ok(info);
    }

    // ============================================
    // POST /management/clear-caches
    // ============================================
    @PostMapping("/clear-caches")
    public ResponseEntity<Void> clearCaches() {
        log.info("Clearing all caches");

        try {
            userStatsRepository.clearAllCaches();
            log.info("All caches cleared successfully");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error clearing caches: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}