package com.bank.recommendation.service;

import com.bank.recommendation.dto.RecommendationDto;
import com.bank.recommendation.enums.ComparisonOperator;
import com.bank.recommendation.enums.ProductType;
import com.bank.recommendation.enums.TransactionType;
import com.bank.recommendation.repository.UserStatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CreditRuleSet implements RecommendationRuleSet {

    private static final Logger log = LoggerFactory.getLogger(CreditRuleSet.class);
    private final UserStatsRepository repository;

    private static final String PRODUCT_ID = "ab138afb-f3ba-4a93-b74f-0fcee86d447f";
    private static final String PRODUCT_NAME = "Простой кредит";
    private static final String PRODUCT_TEXT =
            "Откройте мир выгодных кредитов с нами! " +
                    "Ищете способ быстро и без лишних хлопот получить нужную сумму?";

    public CreditRuleSet(UserStatsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        // Правила для Простой кредит:
        // 1. Пользователь НЕ использует продукты с типом CREDIT
        // 2. Сумма пополнений по DEBIT > сумма трат по DEBIT
        // 3. Сумма трат по DEBIT > 100 000 ₽

        boolean hasCredit = repository.isUserOfProduct(userId, ProductType.CREDIT);

        // Сумма пополнений по DEBIT > сумма трат по DEBIT
        boolean depositsGreaterThanWithdrawals = repository.compareDepositWithdrawSum(
                userId,
                ProductType.DEBIT,
                ComparisonOperator.GT
        );

        // Сумма трат по DEBIT > 100 000 ₽
        boolean withdrawalsGreaterThan100000 = repository.compareTransactionSum(
                userId,
                ProductType.DEBIT,
                TransactionType.WITHDRAWAL,
                ComparisonOperator.GT,
                100000
        );

        if (!hasCredit && depositsGreaterThanWithdrawals && withdrawalsGreaterThan100000) {
            log.debug("User {} qualifies for Credit", userId);
            return Optional.of(new RecommendationDto(PRODUCT_NAME, PRODUCT_ID, PRODUCT_TEXT));
        }

        return Optional.empty();
    }
}