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
public class SavingRuleSet implements RecommendationRuleSet {

    private static final Logger log = LoggerFactory.getLogger(SavingRuleSet.class);
    private final UserStatsRepository repository;

    private static final String PRODUCT_ID = "59efc529-2fff-41af-baff-90ccd7402925";
    private static final String PRODUCT_NAME = "Top Saving";
    private static final String PRODUCT_TEXT =
            "Откройте свою собственную «Копилку» с нашим банком! " +
                    "«Копилка» — это уникальный банковский инструмент, который поможет вам легко и удобно накапливать деньги.";

    public SavingRuleSet(UserStatsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        // Правила для Top Saving:
        // 1. Пользователь использует как минимум один продукт с типом DEBIT
        // 2. Сумма пополнений по DEBIT >= 50 000 ИЛИ сумма пополнений по SAVING >= 50 000
        // 3. Сумма пополнений по DEBIT > сумма трат по DEBIT

        boolean hasDebit = repository.isUserOfProduct(userId, ProductType.DEBIT);

        // Сумма пополнений по DEBIT
        boolean debitDepositsGE50000 = repository.compareTransactionSum(
                userId,
                ProductType.DEBIT,
                TransactionType.DEPOSIT,
                ComparisonOperator.GTE,
                50000
        );

        // Сумма пополнений по SAVING
        boolean savingDepositsGE50000 = repository.compareTransactionSum(
                userId,
                ProductType.SAVING,
                TransactionType.DEPOSIT,
                ComparisonOperator.GTE,
                50000
        );

        // Сумма пополнений по DEBIT > сумма трат по DEBIT
        boolean depositsGreaterThanWithdrawals = repository.compareDepositWithdrawSum(
                userId,
                ProductType.DEBIT,
                ComparisonOperator.GT
        );

        boolean condition2 = debitDepositsGE50000 || savingDepositsGE50000;

        if (hasDebit && condition2 && depositsGreaterThanWithdrawals) {
            log.debug("User {} qualifies for Top Saving", userId);
            return Optional.of(new RecommendationDto(PRODUCT_NAME, PRODUCT_ID, PRODUCT_TEXT));
        }

        return Optional.empty();
    }
}