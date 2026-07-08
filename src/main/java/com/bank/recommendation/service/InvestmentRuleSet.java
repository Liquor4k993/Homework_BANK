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
public class InvestmentRuleSet implements RecommendationRuleSet {

    private static final Logger log = LoggerFactory.getLogger(InvestmentRuleSet.class);
    private final UserStatsRepository repository;

    private static final String PRODUCT_ID = "147f6a0f-3b91-413b-ab99-87f081d60d5a";
    private static final String PRODUCT_NAME = "Invest 500";
    private static final String PRODUCT_TEXT =
            "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! " +
                    "Воспользуйтесь налоговыми льготами и начните инвестировать с умом.";

    public InvestmentRuleSet(UserStatsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        // Правила для Invest 500:
        // 1. Пользователь использует как минимум один продукт с типом DEBIT
        // 2. Пользователь НЕ использует продукты с типом INVEST
        // 3. Сумма пополнений продуктов с типом SAVING больше 1000 ₽

        boolean hasDebit = repository.isUserOfProduct(userId, ProductType.DEBIT);
        boolean hasInvest = repository.isUserOfProduct(userId, ProductType.INVEST);
        boolean savingGreater1000 = repository.compareTransactionSum(
                userId,
                ProductType.SAVING,
                TransactionType.DEPOSIT,
                ComparisonOperator.GT,
                1000
        );

        if (hasDebit && !hasInvest && savingGreater1000) {
            log.debug("User {} qualifies for Invest 500", userId);
            return Optional.of(new RecommendationDto(PRODUCT_NAME, PRODUCT_ID, PRODUCT_TEXT));
        }

        return Optional.empty();
    }
}