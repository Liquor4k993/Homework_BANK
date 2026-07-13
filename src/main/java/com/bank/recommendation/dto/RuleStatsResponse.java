package com.bank.recommendation.dto;

import java.util.List;
import java.util.UUID;

public class RuleStatsResponse {

    private List<RuleStat> stats;

    public RuleStatsResponse() {
    }

    public RuleStatsResponse(List<RuleStat> stats) {
        this.stats = stats;
    }

    public List<RuleStat> getStats() {
        return stats;
    }

    public void setStats(List<RuleStat> stats) {
        this.stats = stats;
    }

    public static class RuleStat {
        private UUID ruleId;
        private long count;

        public RuleStat() {
        }

        public RuleStat(UUID ruleId, long count) {
            this.ruleId = ruleId;
            this.count = count;
        }

        public UUID getRuleId() {
            return ruleId;
        }

        public void setRuleId(UUID ruleId) {
            this.ruleId = ruleId;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }
    }
}