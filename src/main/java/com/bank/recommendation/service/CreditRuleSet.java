package com.bank.recommendation.service;

import com.bank.recommendation.dto.RecommendationDto;
import com.bank.recommendation.repository.RecommendationRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class CreditRuleSet implements RecommendationRuleSet {

    private final RecommendationRepository repository;
    private static final String PRODUCT_ID = "ab138afb-f3ba-4a93-b74f-0fcee86d447f";
    private static final String PRODUCT_NAME = "Простой кредит";
    private static final String PRODUCT_TEXT =
            "Откройте мир выгодных кредитов с нами! " +
                    "Ищете способ быстро и без лишних хлопот получить нужную сумму?";

    public CreditRuleSet(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        boolean hasCredit = repository.hasCreditProduct(userId);
        double debitDeposits = repository.getDebitDepositSum(userId);
        double debitWithdrawals = repository.getDebitWithdrawalSum(userId);

        boolean condition2 = debitDeposits > debitWithdrawals;
        boolean condition3 = debitWithdrawals > 100000;

        if (!hasCredit && condition2 && condition3) {
            return Optional.of(new RecommendationDto(PRODUCT_NAME, PRODUCT_ID, PRODUCT_TEXT));
        }
        return Optional.empty();
    }
}