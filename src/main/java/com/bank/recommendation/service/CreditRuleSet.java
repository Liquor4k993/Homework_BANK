package com.bank.recommendation.service;

import com.bank.recommendation.dto.RecommendationDto;
import com.bank.recommendation.repository.RecommendationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class CreditRuleSet implements RecommendationRuleSet {

    private static final Logger log = LoggerFactory.getLogger(CreditRuleSet.class);

    private final RecommendationRepository repository;

    private static final String PRODUCT_ID = "ab138afb-f3ba-4a93-b74f-0fcee86d447f";
    private static final String PRODUCT_NAME = "Простой кредит";
    private static final String PRODUCT_TEXT =
            "Откройте мир выгодных кредитов с нами!\n\n" +
                    "Ищете способ быстро и без лишних хлопот получить нужную сумму? " +
                    "Тогда наш выгодный кредит — именно то, что вам нужно! " +
                    "Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту.\n\n" +
                    "Почему выбирают нас:\n" +
                    "• Быстрое рассмотрение заявки. Мы ценим ваше время.\n" +
                    "• Удобное оформление. Подать заявку можно онлайн.\n" +
                    "• Широкий выбор кредитных продуктов.\n\n" +
                    "Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!";

    public CreditRuleSet(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        log.info("Checking CREDIT rules for user: {}", userId);

        RecommendationRepository.UserStats stats = repository.getUserStats(userId);

        // Правила для Простой кредит:
        // 1. Пользователь НЕ использует продукты с типом CREDIT
        // 2. Сумма пополнений по DEBIT > сумма трат по DEBIT
        // 3. Сумма трат по DEBIT > 100 000 ₽

        boolean condition1 = !stats.isHasCredit();
        boolean condition2 = stats.getDebitDeposits() > stats.getDebitWithdrawals();
        boolean condition3 = stats.getDebitWithdrawals() > 100000;

        log.debug("Conditions for Credit - !hasCredit: {}, condition2: {} (dep > with={}), condition3: {} (with > 100000={})",
                condition1, condition2, stats.getDebitDeposits() > stats.getDebitWithdrawals(),
                condition3, stats.getDebitWithdrawals() > 100000);

        if (condition1 && condition2 && condition3) {
            log.info("✅ User {} qualifies for Credit", userId);
            return Optional.of(new RecommendationDto(PRODUCT_NAME, PRODUCT_ID, PRODUCT_TEXT));
        }

        log.info("❌ User {} does NOT qualify for Credit", userId);
        return Optional.empty();
    }
}