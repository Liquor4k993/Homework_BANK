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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
public class UserStatsRepository {

    private static final Logger log = LoggerFactory.getLogger(UserStatsRepository.class);

    private final JdbcTemplate jdbcTemplate;

    // Кэши для каждого типа запроса
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

    // USER_OF
    public boolean isUserOfProduct(UUID userId, ProductType productType) {
        String cacheKey = userId + "_" + productType;
        return userOfCache.get(cacheKey, key -> {
            String sql = """
                SELECT COUNT(*) > 0 
                FROM transactions t 
                JOIN products p ON t.product_id = p.id 
                WHERE t.user_id = ? AND p.type = ?
            """;
            return jdbcTemplate.queryForObject(sql, Boolean.class,
                    userId.toString(), productType.name());
        });
    }

    // ACTIVE_USER_OF
    public boolean isActiveUserOfProduct(UUID userId, ProductType productType) {
        String cacheKey = userId + "_" + productType;
        return activeUserOfCache.get(cacheKey, key -> {
            String sql = """
                SELECT COUNT(*) >= 5 
                FROM transactions t 
                JOIN products p ON t.product_id = p.id 
                WHERE t.user_id = ? AND p.type = ?
            """;
            return jdbcTemplate.queryForObject(sql, Boolean.class,
                    userId.toString(), productType.name());
        });
    }

    // TRANSACTION_SUM_COMPARE
    public boolean compareTransactionSum(UUID userId, ProductType productType,
                                         TransactionType transactionType,
                                         ComparisonOperator operator, int constant) {
        String cacheKey = String.format("%s_%s_%s_%s_%d",
                userId, productType, transactionType, operator, constant);

        return transactionSumCompareCache.get(cacheKey, key -> {
            String sql = """
                SELECT COALESCE(SUM(t.amount), 0)
                FROM transactions t 
                JOIN products p ON t.product_id = p.id 
                WHERE t.user_id = ? AND p.type = ? AND t.type = ?
            """;

            Double sum = jdbcTemplate.queryForObject(sql, Double.class,
                    userId.toString(), productType.name(), transactionType.name());

            return switch (operator) {
                case GT -> sum > constant;
                case LT -> sum < constant;
                case EQ -> sum == constant;
                case GTE -> sum >= constant;
                case LTE -> sum <= constant;
            };
        });
    }

    // TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW
    public boolean compareDepositWithdrawSum(UUID userId, ProductType productType,
                                             ComparisonOperator operator) {
        String cacheKey = userId + "_" + productType + "_" + operator;

        return transactionSumCompareDepositWithdrawCache.get(cacheKey, key -> {
            String sql = """
                SELECT 
                    COALESCE(SUM(CASE WHEN t.type = 'DEPOSIT' THEN t.amount END), 0) as deposit_sum,
                    COALESCE(SUM(CASE WHEN t.type = 'WITHDRAWAL' THEN t.amount END), 0) as withdraw_sum
                FROM transactions t 
                JOIN products p ON t.product_id = p.id 
                WHERE t.user_id = ? AND p.type = ?
            """;

            var result = jdbcTemplate.queryForMap(sql, userId.toString(), productType.name());
            Double depositSum = ((Number) result.get("deposit_sum")).doubleValue();
            Double withdrawSum = ((Number) result.get("withdraw_sum")).doubleValue();

            return switch (operator) {
                case GT -> depositSum > withdrawSum;
                case LT -> depositSum < withdrawSum;
                case EQ -> depositSum == withdrawSum;
                case GTE -> depositSum >= withdrawSum;
                case LTE -> depositSum <= withdrawSum;
            };
        });
    }
}