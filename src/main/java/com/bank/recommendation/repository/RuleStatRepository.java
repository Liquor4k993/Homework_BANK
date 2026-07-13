package com.bank.recommendation.repository;

import com.bank.recommendation.entity.RuleEntity;
import com.bank.recommendation.entity.RuleStatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RuleStatRepository extends JpaRepository<RuleStatEntity, UUID> {

    Optional<RuleStatEntity> findByRule(RuleEntity rule);

    Optional<RuleStatEntity> findByRuleId(UUID ruleId);

    // Исправленный метод - возвращает int (количество обновленных строк)
    @Modifying
    @Transactional
    @Query("UPDATE RuleStatEntity r SET r.count = r.count + 1 WHERE r.rule.id = :ruleId")
    int incrementCount(@Param("ruleId") UUID ruleId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RuleStatEntity r WHERE r.rule.id = :ruleId")
    void deleteByRuleId(@Param("ruleId") UUID ruleId);
}