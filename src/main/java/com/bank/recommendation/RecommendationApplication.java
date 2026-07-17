package com.bank.recommendation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RecommendationApplication {

    private static final Logger log = LoggerFactory.getLogger(RecommendationApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RecommendationApplication.class, args);
        log.info("========================================");
        log.info("  🏦 Star Bank Recommendation Service");
        log.info("  ✅ Started successfully!");
        log.info("  📍 GET  /recommendation/{{userId}}");
        log.info("  📍 POST /rule");
        log.info("  📍 GET  /rule");
        log.info("  📍 GET  /rule/stats");
        log.info("  📍 DELETE /rule/{{id}}");
        log.info("  📍 POST /management/clear-caches");
        log.info("  📍 GET  /management/info");
        log.info("  🤖 Telegram Bot: @{{bot_username}}");
        log.info("========================================");
    }
}