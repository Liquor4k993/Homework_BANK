package com.bank.recommendation.bot;

import com.bank.recommendation.config.BotConfig;
import com.bank.recommendation.dto.RecommendationDto;
import com.bank.recommendation.repository.UserRepository;
import com.bank.recommendation.service.RecommendationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class RecommendationBot extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(RecommendationBot.class);
    private static final String RECOMMEND_COMMAND = "/recommend";

    private final RecommendationService recommendationService;
    private final UserRepository userRepository;
    private final BotConfig botConfig;

    @Autowired
    public RecommendationBot(RecommendationService recommendationService,
                             UserRepository userRepository,
                             BotConfig botConfig) {
        this.recommendationService = recommendationService;
        this.userRepository = userRepository;
        this.botConfig = botConfig;
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotUsername();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String messageText = update.getMessage().getText();

            log.info("Received message from chat {}: {}", chatId, messageText);

            try {
                // Точное совпадение команды /recommend (с пробелом или без)
                if (messageText.startsWith(RECOMMEND_COMMAND + " ") ||
                        messageText.equals(RECOMMEND_COMMAND)) {

                    String[] parts = messageText.split(" ", 2);
                    if (parts.length < 2 || parts[1].trim().isEmpty()) {
                        sendMessage(chatId, "❌ Пожалуйста, укажите имя пользователя.\n" +
                                "Пример: /recommend Иван Иванов");
                        return;
                    }

                    String username = parts[1].trim();
                    handleRecommendCommand(chatId, username);
                    return;
                }

                sendHelpMessage(chatId);

            } catch (Exception e) {
                log.error("Error processing message from {}: {}", chatId, e.getMessage(), e);
                sendMessage(chatId, "❌ Произошла ошибка. Пожалуйста, попробуйте позже.");
            }
        }
    }

    /**
     * Обрабатывает команду /recommend
     */
    private void handleRecommendCommand(String chatId, String username) {
        log.info("Processing /recommend for username: {}", username);

        String[] nameParts = username.split(" ", 2);
        if (nameParts.length < 2) {
            sendMessage(chatId, "❌ Пожалуйста, укажите имя и фамилию через пробел.\n" +
                    "Пример: /recommend Иван Иванов");
            return;
        }

        String firstName = nameParts[0];
        String lastName = nameParts[1];

        try {
            // Поиск пользователя через репозиторий
            List<Map<String, Object>> users = userRepository.findUserByNameAndSurname(firstName, lastName);

            if (users.isEmpty()) {
                sendMessage(chatId, "❌ Пользователь не найден");
                return;
            }

            if (users.size() > 1) {
                sendMessage(chatId, "❌ Пользователь не найден");
                return;
            }

            // Найден ровно один пользователь
            Map<String, Object> user = users.get(0);
            String userId = user.get("id").toString();
            String name = user.get("name").toString();
            String surname = user.get("surname").toString();

            // Получаем рекомендации
            List<RecommendationDto> recommendations =
                    recommendationService.getRecommendations(UUID.fromString(userId));

            // Формируем ответ
            String response = buildRecommendationResponse(name, surname, recommendations);
            sendMessage(chatId, response);

        } catch (Exception e) {
            log.error("Error processing /recommend for {}: {}", username, e.getMessage(), e);
            sendMessage(chatId, "❌ Произошла ошибка при поиске пользователя. Пожалуйста, попробуйте позже.");
        }
    }

    /**
     * Формирует ответ с рекомендациями
     */
    private String buildRecommendationResponse(String name, String surname,
                                               List<RecommendationDto> recommendations) {
        StringBuilder response = new StringBuilder();
        response.append("👋 Здравствуйте, ").append(name).append(" ").append(surname).append("!\n\n");

        if (recommendations.isEmpty()) {
            response.append("📭 Новых продуктов для вас нет.\n");
            response.append("Спасибо, что пользуетесь нашими услугами! 😊");
        } else {
            response.append("📦 Новые продукты для вас:\n\n");
            for (int i = 0; i < recommendations.size(); i++) {
                RecommendationDto rec = recommendations.get(i);
                response.append("• *").append(rec.getName()).append("*\n");
                response.append("  ").append(rec.getText()).append("\n\n");
            }
            response.append("💡 Для получения более подробной информации посетите наш сайт или обратитесь в отделение банка.");
        }

        return response.toString();
    }

    /**
     * Отправляет приветственное сообщение со справкой
     */
    private void sendHelpMessage(String chatId) {
        StringBuilder help = new StringBuilder();
        help.append("🤖 *Банк «Стар» - Рекомендательная система*\n\n");
        help.append("Я помогу вам подобрать лучшие банковские продукты!\n\n");
        help.append("*Доступные команды:*\n");
        help.append("/recommend <Имя Фамилия> - Получить персональные рекомендации\n\n");
        help.append("*Пример:*\n");
        help.append("/recommend Иван Иванов\n\n");
        help.append("ℹ️ Для получения рекомендаций укажите ваши имя и фамилию, как в базе банка.");

        sendMessage(chatId, help.toString());
    }

    /**
     * Отправляет сообщение в чат
     */
    private void sendMessage(String chatId, String text) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(text);
            message.setParseMode("Markdown");

            execute(message);
            log.debug("Sent message to chat {}", chatId);

        } catch (TelegramApiException e) {
            log.error("Error sending message to {}: {}", chatId, e.getMessage(), e);
        }
    }
}