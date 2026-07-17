package com.bank.recommendation.config;

import com.bank.recommendation.bot.RecommendationBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class BotRegistrar {

    private static final Logger log = LoggerFactory.getLogger(BotRegistrar.class);

    private final TelegramBotsApi telegramBotsApi;
    private final RecommendationBot bot;

    public BotRegistrar(TelegramBotsApi telegramBotsApi, RecommendationBot bot) {
        this.telegramBotsApi = telegramBotsApi;
        this.bot = bot;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void registerBot() {
        try {
            telegramBotsApi.registerBot(bot);
            log.info("✅ Telegram Bot registered successfully!");
            log.info("Bot username: @{}", bot.getBotUsername());
        } catch (TelegramApiException e) {
            log.error("❌ Failed to register Telegram Bot: {}", e.getMessage(), e);
        }
    }
}