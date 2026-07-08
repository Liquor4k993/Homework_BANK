package com.bank.recommendation.repository;

import com.bank.recommendation.entity.RuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface RuleRepository extends JpaRepository<RuleEntity, UUID> {
}