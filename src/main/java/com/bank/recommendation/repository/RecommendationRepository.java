package com.bank.recommendation.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public class RecommendationRepository {

    private final JdbcTemplate jdbcTemplate;

    // Инжектим JdbcTemplate через конструктор
    public RecommendationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean hasDebitProduct(UUID userId) {
        String sql = """
            SELECT COUNT(*) > 0 
            FROM transactions t 
            JOIN products p ON t.product_id = p.id 
            WHERE t.user_id = ? AND p.type = 'DEBIT'
        """;
        return jdbcTemplate.queryForObject(sql, Boolean.class, userId.toString());
    }

    public boolean hasInvestProduct(UUID userId) {
        String sql = """
            SELECT COUNT(*) > 0 
            FROM transactions t 
            JOIN products p ON t.product_id = p.id 
            WHERE t.user_id = ? AND p.type = 'INVEST'
        """;
        return jdbcTemplate.queryForObject(sql, Boolean.class, userId.toString());
    }

    public double getSavingDepositSum(UUID userId) {
        String sql = """
            SELECT COALESCE(SUM(t.amount), 0)
            FROM transactions t 
            JOIN products p ON t.product_id = p.id 
            WHERE t.user_id = ? AND p.type = 'SAVING' AND t.type = 'DEPOSIT'
        """;
        return jdbcTemplate.queryForObject(sql, Double.class, userId.toString());
    }

    public double getDebitDepositSum(UUID userId) {
        String sql = """
            SELECT COALESCE(SUM(t.amount), 0)
            FROM transactions t 
            JOIN products p ON t.product_id = p.id 
            WHERE t.user_id = ? AND p.type = 'DEBIT' AND t.type = 'DEPOSIT'
        """;
        return jdbcTemplate.queryForObject(sql, Double.class, userId.toString());
    }

    public double getDebitWithdrawalSum(UUID userId) {
        String sql = """
            SELECT COALESCE(SUM(t.amount), 0)
            FROM transactions t 
            JOIN products p ON t.product_id = p.id 
            WHERE t.user_id = ? AND p.type = 'DEBIT' AND t.type = 'WITHDRAWAL'
        """;
        return jdbcTemplate.queryForObject(sql, Double.class, userId.toString());
    }

    public boolean hasCreditProduct(UUID userId) {
        String sql = """
            SELECT COUNT(*) > 0 
            FROM transactions t 
            JOIN products p ON t.product_id = p.id 
            WHERE t.user_id = ? AND p.type = 'CREDIT'
        """;
        return jdbcTemplate.queryForObject(sql, Boolean.class, userId.toString());
    }
}