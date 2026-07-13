package com.bank.recommendation.enums;

public enum TransactionType {
    DEPOSIT,
    WITHDRAW,   // Основной вариант (как в ТЗ)
    WITHDRAWAL; // Добавляем для совместимости с БД

    public static TransactionType fromString(String value) {
        if (value == null) return null;

        // Поддерживаем оба варианта
        if (value.equalsIgnoreCase("WITHDRAW") || value.equalsIgnoreCase("WITHDRAWAL")) {
            return WITHDRAW;
        }
        if (value.equalsIgnoreCase("DEPOSIT")) {
            return DEPOSIT;
        }
        throw new IllegalArgumentException("Unknown transaction type: " + value);
    }
}