package com.bank.recommendation.dto;

import java.util.List;

public class RecommendationResponse {
    private String userId;
    private List<RecommendationDto> recommendations;

    public RecommendationResponse() {
    }

    public RecommendationResponse(String userId, List<RecommendationDto> recommendations) {
        this.userId = userId;
        this.recommendations = recommendations;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<RecommendationDto> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<RecommendationDto> recommendations) {
        this.recommendations = recommendations;
    }
}