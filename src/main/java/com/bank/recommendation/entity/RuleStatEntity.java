package com.bank.recommendation.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "rule_stats")
public class RuleStatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private RuleEntity rule;

    @Column(name = "count", nullable = false)
    private long count = 0;

    public RuleStatEntity() {
    }

    public RuleStatEntity(RuleEntity rule) {
        this.rule = rule;
        this.count = 0;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public RuleEntity getRule() {
        return rule;
    }

    public void setRule(RuleEntity rule) {
        this.rule = rule;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void increment() {
        this.count++;
    }
}