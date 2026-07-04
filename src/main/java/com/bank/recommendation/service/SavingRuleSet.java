package com.bank.recommendation.service;

import com.bank.recommendation.dto.RecommendationDto;
import com.bank.recommendation.repository.RecommendationRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class SavingRuleSet implements RecommendationRuleSet {

    private final RecommendationRepository repository;
    private static final String PRODUCT_ID = "59efc529-2fff-41af-baff-90ccd7402925";
    private static final String PRODUCT_NAME = "Top Saving";
    private static final String PRODUCT_TEXT =
            "Откройте свою собственную «Копилку» с нашим банком! " +
                    "«Копилка» — это уникальный банковский инструмент, который поможет вам легко и удобно накапливать деньги.";

    public SavingRuleSet(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        boolean hasDebit = repository.hasDebitProduct(userId);
        double debitDeposits = repository.getDebitDepositSum(userId);
        double savingDeposits = repository.getSavingDepositSum(userId);
        double debitWithdrawals = repository.getDebitWithdrawalSum(userId);

        boolean condition2 = (debitDeposits >= 50000) || (savingDeposits >= 50000);
        boolean condition3 = debitDeposits > debitWithdrawals;

        if (hasDebit && condition2 && condition3) {
            return Optional.of(new RecommendationDto(PRODUCT_NAME, PRODUCT_ID, PRODUCT_TEXT));
        }
        return Optional.empty();
    }
}