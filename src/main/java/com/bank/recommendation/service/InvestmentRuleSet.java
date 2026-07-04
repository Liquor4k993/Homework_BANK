package com.bank.recommendation.service;

import com.bank.recommendation.dto.RecommendationDto;
import com.bank.recommendation.repository.RecommendationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class InvestmentRuleSet implements RecommendationRuleSet {

    private static final Logger log = LoggerFactory.getLogger(InvestmentRuleSet.class);

    private final RecommendationRepository repository;

    private static final String PRODUCT_ID = "147f6a0f-3b91-413b-ab99-87f081d60d5a";
    private static final String PRODUCT_NAME = "Invest 500";
    private static final String PRODUCT_TEXT =
            "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! " +
                    "Воспользуйтесь налоговыми льготами и начните инвестировать с умом. " +
                    "Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. " +
                    "Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. " +
                    "Откройте ИИС сегодня и станьте ближе к финансовой независимости!";

    public InvestmentRuleSet(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        log.info("Checking INVEST 500 rules for user: {}", userId);

        // Вся статистика одним запросом
        RecommendationRepository.UserStats stats = repository.getUserStats(userId);

        // Правила для Invest 500:
        // 1. Пользователь использует как минимум один продукт с типом DEBIT
        // 2. Пользователь НЕ использует продукты с типом INVEST
        // 3. Сумма пополнений продуктов с типом SAVING больше 1000 ₽

        boolean condition1 = stats.isHasDebit();
        boolean condition2 = !stats.isHasInvest();
        boolean condition3 = stats.getSavingDeposits() > 1000;

        log.debug("Conditions for Invest 500 - hasDebit: {}, !hasInvest: {}, savingDeposits > 1000: {} (savingDeposits={})",
                condition1, condition2, condition3, stats.getSavingDeposits());

        if (condition1 && condition2 && condition3) {
            log.info("✅ User {} qualifies for Invest 500", userId);
            return Optional.of(new RecommendationDto(PRODUCT_NAME, PRODUCT_ID, PRODUCT_TEXT));
        }

        log.info("❌ User {} does NOT qualify for Invest 500", userId);
        return Optional.empty();
    }
}