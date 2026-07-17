# 🏦 Банк «Стар» - Рекомендательная система

[![Java](https://img.shields.io/badge/Java-17-blue)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-green)](https://spring.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14-blue)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-Proprietary-red)]()

---

## 📖 О проекте

**Star Bank Recommendation Service** - это сервис рекомендаций для банка «Стар».
Система помогает клиентам банка находить подходящие банковские продукты на основе
их транзакций и поведения.

Сервис использует:
- **Статические правила** (Invest 500, Top Saving, Простой кредит) - встроены в код
- **Динамические правила** - создаются через API, хранятся в PostgreSQL
- **Telegram бота** - для удобного доступа к рекомендациям

---

## 🎯 Основные возможности

| Возможность | Описание |
|-------------|----------|
| ✅ REST API | Получение рекомендаций по ID пользователя |
| ✅ Telegram бот | Команда `/recommend Имя Фамилия` |
| ✅ Динамические правила | Создание/удаление правил через API |
| ✅ Статистика | Отслеживание эффективности правил |
| ✅ Кэширование | Caffeine Cache для высокой производительности |
| ✅ Две БД | H2 (чтение) + PostgreSQL (чтение/запись) |

---

## 🚀 Быстрый старт

### 1. Клонируйте репозиторий
```bash
git clone https://github.com/ваш-репозиторий/recommendation.git
cd recommendation