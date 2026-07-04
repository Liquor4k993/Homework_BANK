package com.bank.recommendation.dto;

public class RecommendationDto {
    private String name;
    private String id;
    private String text;

    // Конструктор по умолчанию (нужен для Jackson)
    public RecommendationDto() {
    }

    // Конструктор с параметрами
    public RecommendationDto(String name, String id, String text) {
        this.name = name;
        this.id = id;
        this.text = text;
    }

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}