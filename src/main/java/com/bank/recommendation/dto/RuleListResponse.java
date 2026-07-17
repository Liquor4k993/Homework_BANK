package com.bank.recommendation.dto;

import java.util.List;

public class RuleListResponse {
    private List<RuleResponse> data;

    public RuleListResponse() {
    }

    public RuleListResponse(List<RuleResponse> data) {
        this.data = data;
    }

    public List<RuleResponse> getData() {
        return data;
    }

    public void setData(List<RuleResponse> data) {
        this.data = data;
    }
}