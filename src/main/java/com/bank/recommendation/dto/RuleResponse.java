package com.bank.recommendation.dto;

import java.util.List;
import java.util.UUID;

public class RuleResponse {
    private UUID id;
    private String productName;
    private String productId;
    private String productText;
    private List<QueryDto> rule;

    public RuleResponse() {
    }

    public RuleResponse(UUID id, String productName, String productId, String productText, List<QueryDto> rule) {
        this.id = id;
        this.productName = productName;
        this.productId = productId;
        this.productText = productText;
        this.rule = rule;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductText() {
        return productText;
    }

    public void setProductText(String productText) {
        this.productText = productText;
    }

    public List<QueryDto> getRule() {
        return rule;
    }

    public void setRule(List<QueryDto> rule) {
        this.rule = rule;
    }
}