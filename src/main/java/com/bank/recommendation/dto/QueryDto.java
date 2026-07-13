package com.bank.recommendation.dto;

import com.bank.recommendation.enums.QueryType;
import java.util.List;

public class QueryDto {
    private QueryType query;
    private List<String> arguments;
    private boolean negate;

    public QueryDto() {
    }

    public QueryDto(QueryType query, List<String> arguments, boolean negate) {
        this.query = query;
        this.arguments = arguments;
        this.negate = negate;
    }

    public QueryType getQuery() {
        return query;
    }

    public void setQuery(QueryType query) {
        this.query = query;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    public boolean isNegate() {
        return negate;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
    }
}