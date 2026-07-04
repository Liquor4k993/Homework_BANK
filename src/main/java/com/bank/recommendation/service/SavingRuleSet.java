package com.bank.recommendation.service;

import com.bank.recommendation.dto.RecommendationDto;
import com.bank.recommendation.repository.RecommendationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class SavingRuleSet implements RecommendationRuleSet {

    private static final Logger log = LoggerFactory.getLogger(SavingRuleSet.class);

    private final RecommendationRepository repository;

    private static final String PRODUCT_ID = "59efc529-2fff-41af-baff-90ccd7402925";
    private static final String PRODUCT_NAME = "Top Saving";
    private static final String PRODUCT_TEXT =
            "Откройте свою собственную «Копилку» с нашим банком! " +
                    "«Копилка» — это уникальный банковский инструмент, который поможет вам легко и удобно накапливать деньги на важные цели. " +
                    "Больше никаких забытых чеков и потерянных квитанций — всё под контролем!\n\n" +
                    "Преимущества «Копилки»:\n" +
                    "• Накопление средств на конкретные цели. Установите лимит и срок накопления.\n" +
                    "• Прозрачность и контроль. Отслеживайте свои доходы и расходы.\n" +
                    "• Безопасность и надежность. Ваши средства находятся под защитой банка.\n\n" +
                    "Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!";

    public SavingRuleSet(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        log.info("Checking TOP SAVING rules for user: {}", userId);

        RecommendationRepository.UserStats stats = repository.getUserStats(userId);

        // Правила для Top Saving:
        // 1. Пользователь использует как минимум один продукт с типом DEBIT
        // 2. Сумма пополнений по DEBIT >= 50 000 ИЛИ сумма пополнений по SAVING >= 50 000
        // 3. Сумма пополнений по DEBIT > сумма трат по DEBIT

        boolean condition1 = stats.isHasDebit();
        boolean condition2 = (stats.getDebitDeposits() >= 50000) || (stats.getSavingDeposits() >= 50000);
        boolean condition3 = stats.getDebitDeposits() > stats.getDebitWithdrawals();

        log.debug("Conditions for Top Saving - hasDebit: {}, condition2: {} (debitDep={}, savingDep={}), condition3: {} (dep > with={})",
                condition1, condition2, stats.getDebitDeposits(), stats.getSavingDeposits(),
                condition3, stats.getDebitDeposits() > stats.getDebitWithdrawals());

        if (condition1 && condition2 && condition3) {
            log.info("✅ User {} qualifies for Top Saving", userId);
            return Optional.of(new RecommendationDto(PRODUCT_NAME, PRODUCT_ID, PRODUCT_TEXT));
        }

        log.info("❌ User {} does NOT qualify for Top Saving", userId);
        return Optional.empty();
    }
}