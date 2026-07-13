package com.bank.recommendation.repository;

import com.bank.recommendation.enums.ComparisonOperator;
import com.bank.recommendation.enums.ProductType;
import com.bank.recommendation.enums.TransactionType;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
public class UserStatsRepository {

    private static final Logger log = LoggerFactory.getLogger(UserStatsRepository.class);

    private final JdbcTemplate jdbcTemplate;

    private final Cache<String, Boolean> userOfCache;
    private final Cache<String, Boolean> activeUserOfCache;
    private final Cache<String, Boolean> transactionSumCompareCache;
    private final Cache<String, Boolean> transactionSumCompareDepositWithdrawCache;

    public UserStatsRepository(@Qualifier("h2JdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        this.userOfCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .recordStats()
                .build();

        this.activeUserOfCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .recordStats()
                .build();

        this.transactionSumCompareCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .recordStats()
                .build();

        this.transactionSumCompareDepositWithdrawCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .recordStats()
                .build();
    }

    public boolean isUserOfProduct(UUID userId, ProductType productType) {
        String cacheKey = userId + "_" + productType;
        return userOfCache.get(cacheKey, key -> {
            String sql = """
                SELECT COUNT(*) > 0 
                FROM transactions t 
                JOIN products p ON t.product_id = p.id 
                WHERE t.user_id = ? AND p.type = ?
            """;
            Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class,
                    userId.toString(), productType.name());
            return result != null && result;
        });
    }

    public boolean isActiveUserOfProduct(UUID userId, ProductType productType) {
        String cacheKey = userId + "_" + productType;
        return activeUserOfCache.get(cacheKey, key -> {
            String sql = """
                SELECT COUNT(*) >= 5 
                FROM transactions t 
                JOIN products p ON t.product_id = p.id 
                WHERE t.user_id = ? AND p.type = ?
            """;
            Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class,
                    userId.toString(), productType.name());
            return result != null && result;
        });
    }

    public boolean compareTransactionSum(UUID userId, ProductType productType,
                                         TransactionType transactionType,
                                         ComparisonOperator operator, int constant) {
        String cacheKey = String.format("%s_%s_%s_%s_%d",
                userId, productType, transactionType, operator, constant);

        return transactionSumCompareCache.get(cacheKey, key -> {
            // Используем WITHDRAW для запроса (так как в БД используется WITHDRAW)
            String transactionTypeName = transactionType == TransactionType.WITHDRAWAL ?
                    "WITHDRAW" : transactionType.name();

            String sql = """
                SELECT COALESCE(SUM(t.amount), 0)
                FROM transactions t 
                JOIN products p ON t.product_id = p.id 
                WHERE t.user_id = ? AND p.type = ? AND t.type = ?
            """;

            Double sum = jdbcTemplate.queryForObject(sql, Double.class,
                    userId.toString(), productType.name(), transactionTypeName);

            if (sum == null) sum = 0.0;

            return switch (operator) {
                case GT -> sum > constant;
                case LT -> sum < constant;
                case EQ -> Math.abs(sum - constant) < 0.001;
                case GTE -> sum >= constant;
                case LTE -> sum <= constant;
            };
        });
    }

    public boolean compareDepositWithdrawSum(UUID userId, ProductType productType,
                                             ComparisonOperator operator) {
        String cacheKey = userId + "_" + productType + "_" + operator;

        return transactionSumCompareDepositWithdrawCache.get(cacheKey, key -> {
            String sql = """
                SELECT 
                    COALESCE(SUM(CASE WHEN t.type = 'DEPOSIT' THEN t.amount END), 0) as deposit_sum,
                    COALESCE(SUM(CASE WHEN t.type = 'WITHDRAW' THEN t.amount END), 0) as withdraw_sum
                FROM transactions t 
                JOIN products p ON t.product_id = p.id 
                WHERE t.user_id = ? AND p.type = ?
            """;

            Map<String, Object> result = jdbcTemplate.queryForMap(sql, userId.toString(), productType.name());
            Double depositSum = ((Number) result.get("deposit_sum")).doubleValue();
            Double withdrawSum = ((Number) result.get("withdraw_sum")).doubleValue();

            return switch (operator) {
                case GT -> depositSum > withdrawSum;
                case LT -> depositSum < withdrawSum;
                case EQ -> Math.abs(depositSum - withdrawSum) < 0.001;
                case GTE -> depositSum >= withdrawSum;
                case LTE -> depositSum <= withdrawSum;
            };
        });
    }

    // ============================================
    // УПРАВЛЕНИЕ КЭШЕМ
    // ============================================

    public void clearAllCaches() {
        userOfCache.invalidateAll();
        activeUserOfCache.invalidateAll();
        transactionSumCompareCache.invalidateAll();
        transactionSumCompareDepositWithdrawCache.invalidateAll();
        log.info("All caches cleared");
    }

    public void clearCacheForUser(UUID userId) {
        userOfCache.asMap().keySet().stream()
                .filter(key -> key.startsWith(userId.toString()))
                .forEach(userOfCache::invalidate);

        activeUserOfCache.asMap().keySet().stream()
                .filter(key -> key.startsWith(userId.toString()))
                .forEach(activeUserOfCache::invalidate);

        transactionSumCompareCache.asMap().keySet().stream()
                .filter(key -> key.startsWith(userId.toString()))
                .forEach(transactionSumCompareCache::invalidate);

        transactionSumCompareDepositWithdrawCache.asMap().keySet().stream()
                .filter(key -> key.startsWith(userId.toString()))
                .forEach(transactionSumCompareDepositWithdrawCache::invalidate);

        log.info("Cache cleared for user: {}", userId);
    }

    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("userOfCacheSize", userOfCache.estimatedSize());
        stats.put("activeUserOfCacheSize", activeUserOfCache.estimatedSize());
        stats.put("transactionSumCompareCacheSize", transactionSumCompareCache.estimatedSize());
        stats.put("transactionSumCompareDepositWithdrawCacheSize",
                transactionSumCompareDepositWithdrawCache.estimatedSize());
        return stats;
    }
}