package com.bank.recommendation.dto;

import java.util.List;

public class RuleRequest {
    private String productName;
    private String productId;
    private String productText;
    private List<QueryDto> rule;

    public RuleRequest() {
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