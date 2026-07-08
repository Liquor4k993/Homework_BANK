package com.bank.recommendation.service;

import com.bank.recommendation.dto.RecommendationDto;
import com.bank.recommendation.dto.QueryDto;
import com.bank.recommendation.entity.QueryEntity;
import com.bank.recommendation.entity.RuleEntity;
import com.bank.recommendation.enums.ComparisonOperator;
import com.bank.recommendation.enums.ProductType;
import com.bank.recommendation.enums.QueryType;
import com.bank.recommendation.enums.TransactionType;
import com.bank.recommendation.repository.RuleRepository;
import com.bank.recommendation.repository.UserStatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class DynamicRuleService {

    private static final Logger log = LoggerFactory.getLogger(DynamicRuleService.class);

    private final RuleRepository ruleRepository;
    private final UserStatsRepository statsRepository;

    public DynamicRuleService(RuleRepository ruleRepository,
                              UserStatsRepository statsRepository) {
        this.ruleRepository = ruleRepository;
        this.statsRepository = statsRepository;
    }

    public List<RecommendationDto> checkDynamicRules(UUID userId) {
        List<RecommendationDto> recommendations = new ArrayList<>();

        List<RuleEntity> allRules = ruleRepository.findAll();
        log.debug("Checking {} dynamic rules for user {}", allRules.size(), userId);

        for (RuleEntity rule : allRules) {
            boolean allQueriesPass = true;

            for (QueryEntity query : rule.getQueries()) {
                boolean result = evaluateQuery(userId, query);
                if (!result) {
                    allQueriesPass = false;
                    break;
                }
            }

            if (allQueriesPass) {
                log.info("User {} qualifies for dynamic rule: {}", userId, rule.getProductName());
                recommendations.add(new RecommendationDto(
                        rule.getProductName(),
                        rule.getProductId(),
                        rule.getProductText()
                ));
            }
        }

        return recommendations;
    }

    private boolean evaluateQuery(UUID userId, QueryEntity query) {
        QueryType queryType = query.getQueryType();
        List<String> args = query.getArguments();
        boolean negate = query.isNegate();

        boolean result = switch (queryType) {
            case USER_OF -> evaluateUserOf(userId, args);
            case ACTIVE_USER_OF -> evaluateActiveUserOf(userId, args);
            case TRANSACTION_SUM_COMPARE -> evaluateTransactionSumCompare(userId, args);
            case TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW ->
                    evaluateTransactionSumCompareDepositWithdraw(userId, args);
        };

        return negate ? !result : result;
    }

    private boolean evaluateUserOf(UUID userId, List<String> args) {
        ProductType productType = ProductType.valueOf(args.get(0));
        return statsRepository.isUserOfProduct(userId, productType);
    }

    private boolean evaluateActiveUserOf(UUID userId, List<String> args) {
        ProductType productType = ProductType.valueOf(args.get(0));
        return statsRepository.isActiveUserOfProduct(userId, productType);
    }

    private boolean evaluateTransactionSumCompare(UUID userId, List<String> args) {
        ProductType productType = ProductType.valueOf(args.get(0));
        TransactionType transactionType = TransactionType.valueOf(args.get(1));
        ComparisonOperator operator = ComparisonOperator.fromSymbol(args.get(2));
        int constant = Integer.parseInt(args.get(3));

        return statsRepository.compareTransactionSum(
                userId, productType, transactionType, operator, constant
        );
    }

    private boolean evaluateTransactionSumCompareDepositWithdraw(UUID userId, List<String> args) {
        ProductType productType = ProductType.valueOf(args.get(0));
        ComparisonOperator operator = ComparisonOperator.fromSymbol(args.get(1));

        return statsRepository.compareDepositWithdrawSum(userId, productType, operator);
    }

    @Transactional
    public RuleEntity createRule(String productName, String productId,
                                 String productText, List<QueryDto> queries) {
        RuleEntity rule = new RuleEntity();
        rule.setProductName(productName);
        rule.setProductId(productId);
        rule.setProductText(productText);

        List<QueryEntity> queryEntities = queries.stream()
                .map(q -> {
                    QueryEntity entity = new QueryEntity();
                    entity.setQueryType(q.getQuery());
                    entity.setArguments(q.getArguments());
                    entity.setNegate(q.isNegate());
                    return entity;
                })
                .toList();

        rule.setQueries(queryEntities);
        return ruleRepository.save(rule);
    }

    @Transactional
    public void deleteRule(UUID id) {
        if (ruleRepository.existsById(id)) {
            ruleRepository.deleteById(id);
            log.info("Deleted rule with id: {}", id);
        } else {
            throw new IllegalArgumentException("Rule not found with id: " + id);
        }
    }

    public List<RuleEntity> getAllRules() {
        return ruleRepository.findAll();
    }
}