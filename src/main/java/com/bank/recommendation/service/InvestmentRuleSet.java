package com.bank.recommendation.service;

import com.bank.recommendation.dto.RecommendationDto;
import com.bank.recommendation.repository.RecommendationRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class InvestmentRuleSet implements RecommendationRuleSet {

    private final RecommendationRepository repository;
    private static final String PRODUCT_ID = "147f6a0f-3b91-413b-ab99-87f081d60d5a";
    private static final String PRODUCT_NAME = "Invest 500";
    private static final String PRODUCT_TEXT =
            "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! " +
                    "Воспользуйтесь налоговыми льготами и начните инвестировать с умом.";

    public InvestmentRuleSet(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        boolean hasDebit = repository.hasDebitProduct(userId);
        boolean hasInvest = repository.hasInvestProduct(userId);
        double savingSum = repository.getSavingDepositSum(userId);

        if (hasDebit && !hasInvest && savingSum > 1000) {
            return Optional.of(new RecommendationDto(PRODUCT_NAME, PRODUCT_ID, PRODUCT_TEXT));
        }
        return Optional.empty();
    }
}