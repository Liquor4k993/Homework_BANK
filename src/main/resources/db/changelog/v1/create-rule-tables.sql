-- liquibase formatted sql

-- changeset author:1
CREATE TABLE IF NOT EXISTS rules
(
    id
    UUID
    PRIMARY
    KEY
    DEFAULT
    gen_random_uuid
(
),
    product_name VARCHAR
(
    255
) NOT NULL,
    product_id VARCHAR
(
    255
) NOT NULL,
    product_text TEXT
    );

-- changeset author:2
CREATE TABLE IF NOT EXISTS queries
(
    id
    BIGSERIAL
    PRIMARY
    KEY,
    rule_id
    UUID
    NOT
    NULL,
    query_type
    VARCHAR
(
    50
) NOT NULL,
    negate BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_queries_rule FOREIGN KEY
(
    rule_id
) REFERENCES rules
(
    id
) ON DELETE CASCADE
    );

-- changeset author:3
CREATE TABLE IF NOT EXISTS query_arguments
(
    query_id
    BIGINT
    NOT
    NULL,
    argument
    VARCHAR
(
    255
) NOT NULL,
    CONSTRAINT fk_arguments_query FOREIGN KEY
(
    query_id
) REFERENCES queries
(
    id
) ON DELETE CASCADE
    );

-- changeset author:4
CREATE TABLE IF NOT EXISTS rule_stats
(
    id
    UUID
    PRIMARY
    KEY
    DEFAULT
    gen_random_uuid
(
),
    rule_id UUID NOT NULL,
    count BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_stats_rule FOREIGN KEY
(
    rule_id
) REFERENCES rules
(
    id
) ON DELETE CASCADE
    );

-- changeset author:5
CREATE INDEX idx_queries_rule_id ON queries (rule_id);
CREATE INDEX idx_arguments_query_id ON query_arguments (query_id);
CREATE INDEX idx_stats_rule_id ON rule_stats (rule_id);