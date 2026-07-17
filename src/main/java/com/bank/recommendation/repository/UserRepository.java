package com.bank.recommendation.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(@Qualifier("h2JdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Находит пользователя по имени и фамилии
     *
     * @param firstName имя
     * @param lastName  фамилия
     * @return список пользователей (0, 1 или более)
     */
    public List<Map<String, Object>> findUserByNameAndSurname(String firstName, String lastName) {
        String sql = """
                    SELECT id, name, surname 
                    FROM users 
                    WHERE LOWER(name) = LOWER(?) AND LOWER(surname) = LOWER(?)
                """;

        return jdbcTemplate.queryForList(sql, firstName, lastName);
    }

    /**
     * Находит пользователя по ID
     */
    public Map<String, Object> findUserById(UUID userId) {
        String sql = """
                    SELECT id, name, surname 
                    FROM users 
                    WHERE id = ?
                """;

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, userId.toString());
        return results.isEmpty() ? null : results.get(0);
    }
}