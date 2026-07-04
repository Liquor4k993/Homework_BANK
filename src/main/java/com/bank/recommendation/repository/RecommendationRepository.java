package com.bank.recommendation.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.UUID;

@Repository
public class RecommendationRepository {

    private static final Logger log = LoggerFactory.getLogger(RecommendationRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public RecommendationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public UserStats getUserStats(UUID userId) {
        String sql = """
            SELECT 
                COUNT(DISTINCT CASE WHEN p.type = 'DEBIT' THEN t.product_id END) > 0 AS has_debit,
                COUNT(DISTINCT CASE WHEN p.type = 'INVEST' THEN t.product_id END) > 0 AS has_invest,
                COUNT(DISTINCT CASE WHEN p.type = 'CREDIT' THEN t.product_id END) > 0 AS has_credit,
                COALESCE(SUM(CASE WHEN p.type = 'SAVING' AND t.type = 'DEPOSIT' THEN t.amount END), 0) AS saving_deposits,
                COALESCE(SUM(CASE WHEN p.type = 'DEBIT' AND t.type = 'DEPOSIT' THEN t.amount END), 0) AS debit_deposits,
                COALESCE(SUM(CASE WHEN p.type = 'DEBIT' AND t.type = 'WITHDRAWAL' THEN t.amount END), 0) AS debit_withdrawals
            FROM transactions t
            JOIN products p ON t.product_id = p.id
            WHERE t.user_id = ?
        """;

        log.debug("Executing query for user: {}", userId);

        Map<String, Object> result = jdbcTemplate.queryForMap(sql, userId.toString());

        UserStats stats = new UserStats();
        stats.setHasDebit((Boolean) result.get("has_debit"));
        stats.setHasInvest((Boolean) result.get("has_invest"));
        stats.setHasCredit((Boolean) result.get("has_credit"));
        stats.setSavingDeposits(((Number) result.get("saving_deposits")).doubleValue());
        stats.setDebitDeposits(((Number) result.get("debit_deposits")).doubleValue());
        stats.setDebitWithdrawals(((Number) result.get("debit_withdrawals")).doubleValue());

        log.debug("User stats: {}", stats);
        return stats;
    }

    // ВСПОМОГАТЕЛЬНЫЙ КЛАСС ДЛЯ СТАТИСТИКИ

    public static class UserStats {
        private boolean hasDebit;
        private boolean hasInvest;
        private boolean hasCredit;
        private double savingDeposits;
        private double debitDeposits;
        private double debitWithdrawals;

        // Геттеры и сеттеры
        public boolean isHasDebit() { return hasDebit; }
        public void setHasDebit(boolean hasDebit) { this.hasDebit = hasDebit; }

        public boolean isHasInvest() { return hasInvest; }
        public void setHasInvest(boolean hasInvest) { this.hasInvest = hasInvest; }

        public boolean isHasCredit() { return hasCredit; }
        public void setHasCredit(boolean hasCredit) { this.hasCredit = hasCredit; }

        public double getSavingDeposits() { return savingDeposits; }
        public void setSavingDeposits(double savingDeposits) { this.savingDeposits = savingDeposits; }

        public double getDebitDeposits() { return debitDeposits; }
        public void setDebitDeposits(double debitDeposits) { this.debitDeposits = debitDeposits; }

        public double getDebitWithdrawals() { return debitWithdrawals; }
        public void setDebitWithdrawals(double debitWithdrawals) { this.debitWithdrawals = debitWithdrawals; }

        @Override
        public String toString() {
            return "UserStats{" +
                    "hasDebit=" + hasDebit +
                    ", hasInvest=" + hasInvest +
                    ", hasCredit=" + hasCredit +
                    ", savingDeposits=" + savingDeposits +
                    ", debitDeposits=" + debitDeposits +
                    ", debitWithdrawals=" + debitWithdrawals +
                    '}';
        }
    }
}